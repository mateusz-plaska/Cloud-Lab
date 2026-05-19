import { Component, OnInit, inject, signal } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import type { OrderListItem, OrderStatus } from '../../types';

const STATUS_CLASSES: Record<OrderStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  RESERVED: 'bg-blue-100 text-blue-800',
  PICKED: 'bg-purple-100 text-purple-800',
  PACKED: 'bg-orange-100 text-orange-800',
  SHIPPED: 'bg-green-100 text-green-800',
  FAILED: 'bg-red-100 text-red-800',
};

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [RouterLink, SlicePipe],
  templateUrl: './orders.html',
})
export class Orders implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly orderService = inject(OrderService);

  readonly orders = signal<OrderListItem[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Nie udało się pobrać zamówień');
        this.loading.set(false);
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