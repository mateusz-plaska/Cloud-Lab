import { Component, OnInit, inject, signal } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { PickingService } from '../../core/services/picking.service';
import type { OrderListItem } from '../../types';

@Component({
  selector: 'app-picking',
  standalone: true,
  imports: [FormsModule, SlicePipe],
  templateUrl: './picking.html',
})
export class Picking implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly pickingService = inject(PickingService);

  readonly orders = signal<OrderListItem[]>([]);
  readonly loading = signal(true);

  selectedOrderId = '';
  productId = '';
  quantity = 1;
  failReason = '';
  readonly actionLoading = signal(false);
  readonly successMsg = signal('');
  readonly errorMsg = signal('');

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders.filter((o) => o.status === 'RESERVED'));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  async pick(): Promise<void> {
    if (!this.selectedOrderId || !this.productId || this.actionLoading()) return;
    this.clear();
    this.actionLoading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.pickingService.pickItem(this.selectedOrderId, this.productId, this.quantity).subscribe({
          next: () => resolve(),
          error: reject,
        });
      });
      this.successMsg.set('Picking zakończony pomyślnie');
      this.orders.update((list) => list.filter((o) => o.orderId !== this.selectedOrderId));
      this.reset();
    } catch {
      this.errorMsg.set('Operacja nie powiodła się');
    } finally {
      this.actionLoading.set(false);
    }
  }

  async fail(): Promise<void> {
    if (!this.selectedOrderId || !this.productId || !this.failReason || this.actionLoading()) return;
    this.clear();
    this.actionLoading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.pickingService.failItem(this.selectedOrderId, this.productId, this.failReason).subscribe({
          next: () => resolve(),
          error: reject,
        });
      });
      this.successMsg.set('Zamówienie oznaczone jako nieudane');
      this.orders.update((list) => list.filter((o) => o.orderId !== this.selectedOrderId));
      this.reset();
    } catch {
      this.errorMsg.set('Operacja nie powiodła się');
    } finally {
      this.actionLoading.set(false);
    }
  }

  private reset(): void {
    this.selectedOrderId = '';
    this.productId = '';
    this.quantity = 1;
    this.failReason = '';
  }

  private clear(): void {
    this.successMsg.set('');
    this.errorMsg.set('');
  }
}