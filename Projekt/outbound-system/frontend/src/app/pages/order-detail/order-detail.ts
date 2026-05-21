import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { SseService } from '../../core/services/sse.service';
import type { OrderReport, OrderStatus, OrderStatusUpdate, Product, SseEventType } from '../../types';

const STATUS_CLASSES: Record<OrderStatus, string> = {
  PLANNED: 'bg-yellow-100 text-yellow-800',
  IN_PROGRESS: 'bg-blue-100 text-blue-800',
  PACKED: 'bg-orange-100 text-orange-800',
  READY: 'bg-purple-100 text-purple-800',
  COMPLETED: 'bg-green-100 text-green-800',
  FAILED: 'bg-red-100 text-red-800',
};

const EVENT_TO_STATUS: Partial<Record<SseEventType, OrderStatus>> = {
  ORDER_CREATED: 'PLANNED',
  STOCK_RESERVED: 'IN_PROGRESS',
  ALLOCATION_FAILED: 'FAILED',
  ORDER_PICKED: 'COMPLETED',
  PICK_FAILED: 'FAILED',
  PACKING_FINISHED: 'PACKED',
  SHIPMENT_CREATED: 'READY',
};

const EVENT_LABEL: Record<SseEventType, string> = {
  ORDER_CREATED: 'Zamówienie przyjęte',
  STOCK_RESERVED: 'Towar zarezerwowany',
  ALLOCATION_FAILED: 'Błąd rezerwacji towaru',
  ORDER_PICKED: 'Kompletacja zakończona',
  PICK_FAILED: 'Błąd kompletacji',
  PACKING_FINISHED: 'Zamówienie zapakowane',
  SHIPMENT_CREATED: 'Przesyłka nadana',
};

const STATION_LABEL: Record<string, string> = {
  'order-gateway': 'System zamówień',
  reservation: 'Magazyn',
  picking: 'Picking',
  packing: 'Packing',
  shipping: 'Wysyłka',
};

const ERROR_EVENTS = new Set<SseEventType>(['ALLOCATION_FAILED', 'PICK_FAILED']);

export interface ParsedProduct {
  productId: string;
  name: string;
  quantity: number;
}

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [],
  templateUrl: './order-detail.html',
})
export class OrderDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly productService = inject(ProductService);
  private readonly sseService = inject(SseService);
  private readonly destroyRef = inject(DestroyRef);

  readonly report = signal<OrderReport | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly updates = signal<OrderStatusUpdate[]>([]);

  private readonly products = signal<Product[]>([]);
  private readonly productMap = computed(() => new Map(this.products().map((p) => [p.productId, p.name])));

  readonly parsedProducts = computed((): ParsedProduct[] => {
    const report = this.report();
    const map = this.productMap();
    if (!report) return [];
    return report.products
      .map((s) => {
        const m = s.match(/^(.+) \(x(\d+)\)$/);
        if (!m) return null;
        const productId = m[1];
        return { productId, name: map.get(productId) ?? productId.slice(0, 8) + '…', quantity: parseInt(m[2], 10) };
      })
      .filter((p): p is ParsedProduct => p !== null);
  });

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id')!;

    this.productService.getProducts().subscribe({ next: (p) => this.products.set(p) });

    this.orderService.getOrderReport(orderId).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
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
        },
      });
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
}
