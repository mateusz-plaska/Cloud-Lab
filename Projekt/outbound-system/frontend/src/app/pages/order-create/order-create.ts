import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import type { OrderItem } from '../../types';

@Component({
  selector: 'app-order-create',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './order-create.html',
})
export class OrderCreate {
  private readonly auth = inject(AuthService);
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);

  items = signal<OrderItem[]>([{ productId: '', quantity: 1 }]);
  readonly loading = signal(false);
  readonly error = signal('');

  addItem(): void {
    this.items.update((items) => [...items, { productId: '', quantity: 1 }]);
  }

  removeItem(index: number): void {
    this.items.update((items) => items.filter((_, i) => i !== index));
  }

  updateItem(index: number, field: keyof OrderItem, value: string | number): void {
    this.items.update((items) =>
      items.map((item, i) => (i === index ? { ...item, [field]: value } : item)),
    );
  }

  async submit(): Promise<void> {
    const userId = this.auth.user()?.userId;
    if (!userId || this.loading()) return;

    const items = this.items();
    if (items.some((i) => !i.productId.trim())) {
      this.error.set('Uzupełnij wszystkie ID produktów');
      return;
    }

    this.error.set('');
    this.loading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.orderService.createOrder({ userId, items }).subscribe({
          next: () => resolve(),
          error: reject,
        });
      });
      this.router.navigate(['/orders']);
    } catch {
      this.error.set('Nie udało się złożyć zamówienia');
    } finally {
      this.loading.set(false);
    }
  }
}