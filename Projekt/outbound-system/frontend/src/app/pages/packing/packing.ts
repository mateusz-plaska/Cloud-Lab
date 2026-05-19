import { Component, OnInit, inject, signal } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { PackingService } from '../../core/services/packing.service';
import type { BoxSize, OrderListItem } from '../../types';

const BOX_SIZES: BoxSize[] = ['SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE'];

@Component({
  selector: 'app-packing',
  standalone: true,
  imports: [FormsModule, SlicePipe],
  templateUrl: './packing.html',
})
export class Packing implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly packingService = inject(PackingService);

  readonly orders = signal<OrderListItem[]>([]);
  readonly loading = signal(true);
  readonly boxSizes = BOX_SIZES;

  selectedOrderId = '';
  boxSize: BoxSize = 'MEDIUM';
  weight = 1.0;
  readonly actionLoading = signal(false);
  readonly successMsg = signal('');
  readonly errorMsg = signal('');

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders.filter((o) => o.status === 'PICKED'));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  async pack(): Promise<void> {
    if (!this.selectedOrderId || this.actionLoading()) return;
    this.successMsg.set('');
    this.errorMsg.set('');
    this.actionLoading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.packingService
          .finishPacking(this.selectedOrderId, { boxSize: this.boxSize, weight: this.weight })
          .subscribe({ next: () => resolve(), error: reject });
      });
      this.successMsg.set('Packing zakończony pomyślnie');
      this.orders.update((list) => list.filter((o) => o.orderId !== this.selectedOrderId));
      this.selectedOrderId = '';
    } catch {
      this.errorMsg.set('Operacja nie powiodła się');
    } finally {
      this.actionLoading.set(false);
    }
  }
}