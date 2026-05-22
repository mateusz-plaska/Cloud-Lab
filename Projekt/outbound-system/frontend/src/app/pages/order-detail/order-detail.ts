import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { ShipmentService } from '../../core/services/shipment.service';
import { SseService } from '../../core/services/sse.service';
import { parseProducts } from '../../core/utils/product-parser';
import { STATUS_CLASSES, EVENT_TO_STATUS, EVENT_LABEL, STATION_LABEL, ERROR_EVENTS } from '../../core/constants/order.constants';
import type { OrderProduct, OrderReport, OrderStatus, OrderStatusUpdate, Product, Shipment, SseEventType } from '../../types';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [],
  templateUrl: './order-detail.html',
})
export class OrderDetail implements OnInit {
  private readonly location = inject(Location);
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly productService = inject(ProductService);
  private readonly shipmentService = inject(ShipmentService);
  private readonly sseService = inject(SseService);
  private readonly destroyRef = inject(DestroyRef);

  readonly report = signal<OrderReport | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly updates = signal<OrderStatusUpdate[]>([]);

  readonly shipment = signal<Shipment | null>(null);
  readonly shipmentLoading = signal(false);
  readonly shipmentError = signal('');

  private readonly products = signal<Product[]>([]);
  private readonly productMap = computed(() => new Map(this.products().map((p) => [p.productId, p.name])));

  readonly parsedProducts = computed((): OrderProduct[] => {
    const report = this.report();
    if (!report) return [];
    return parseProducts(report.products, this.productMap());
  });

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id')!;

    this.productService.getProducts().subscribe({ next: (p) => this.products.set(p) });

    this.orderService.getOrderReport(orderId).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
        if (report.status === 'READY') {
          this.fetchShipment(orderId);
        }
      },
      error: () => {
        this.error.set('Nie udało się pobrać zamówienia');
        this.loading.set(false);
      },
    });

    this.sseService
      .watch(orderId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (update) => {
          this.updates.update((prev) => [update, ...prev].slice(0, 50));
          const newStatus = EVENT_TO_STATUS[update.eventType];
          if (newStatus) {
            this.report.update((r) => (r ? { ...r, status: newStatus } : r));
          }
          if (update.eventType === 'SHIPMENT_CREATED') {
            this.fetchShipment(orderId);
          }
        },
      });
  }

  private fetchShipment(orderId: string): void {
    this.shipmentLoading.set(true);
    this.shipmentError.set('');
    this.shipmentService.getShipment(orderId).subscribe({
      next: (s) => {
        this.shipment.set(s);
        this.shipmentLoading.set(false);
      },
      error: () => {
        this.shipmentError.set('Nie udało się pobrać danych przesyłki');
        this.shipmentLoading.set(false);
      },
    });
  }

  goBack(): void {
    this.location.back();
  }

  statusClass(status: OrderStatus): string {
    return STATUS_CLASSES[status] ?? 'bg-slate-100 text-slate-800';
  }

  eventLabel(eventType: SseEventType): string {
    return EVENT_LABEL[eventType];
  }

  stationLabel(station: string): string {
    return STATION_LABEL[station] ?? station;
  }

  isErrorEvent(eventType: SseEventType): boolean {
    return ERROR_EVENTS.has(eventType);
  }

  statusForEvent(eventType: SseEventType): OrderStatus | null {
    return EVENT_TO_STATUS[eventType] ?? null;
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString('pl-PL');
  }

  formatCost(cost: number): string {
    return new Intl.NumberFormat('pl-PL', { style: 'currency', currency: 'PLN' }).format(cost);
  }
}
