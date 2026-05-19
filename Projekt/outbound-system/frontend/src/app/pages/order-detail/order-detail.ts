import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { KeyValuePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OrderService } from '../../core/services/order.service';
import { SseService } from '../../core/services/sse.service';
import type { OrderReport, OrderStatus, OrderStatusUpdate } from '../../types';

const STATUS_CLASSES: Record<OrderStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  RESERVED: 'bg-blue-100 text-blue-800',
  PICKED: 'bg-purple-100 text-purple-800',
  PACKED: 'bg-orange-100 text-orange-800',
  SHIPPED: 'bg-green-100 text-green-800',
  FAILED: 'bg-red-100 text-red-800',
};

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [KeyValuePipe],
  templateUrl: './order-detail.html',
})
export class OrderDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly sseService = inject(SseService);
  private readonly destroyRef = inject(DestroyRef);

  readonly report = signal<OrderReport | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly updates = signal<OrderStatusUpdate[]>([]);

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id')!;

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
          this.updates.update((prev) => [update, ...prev].slice(0, 20));
          this.report.update((r) => (r ? { ...r, status: update.status } : r));
        },
      });
  }

  statusClass(status: OrderStatus): string {
    return STATUS_CLASSES[status] ?? 'bg-slate-100 text-slate-800';
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString('pl-PL');
  }
}