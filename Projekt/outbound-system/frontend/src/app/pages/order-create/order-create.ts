import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { StockService } from '../../core/services/stock.service';
import type { OrderItem, Product, StockItem } from '../../types';

interface ProductOption {
  productId: string;
  name: string;
  quantity: number;
}

@Component({
  selector: 'app-order-create',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './order-create.html',
})
export class OrderCreate implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly orderService = inject(OrderService);
  private readonly productService = inject(ProductService);
  private readonly stockService = inject(StockService);
  private readonly router = inject(Router);

  items = signal<OrderItem[]>([{ productId: '', quantity: 1 }]);
  readonly loading = signal(false);
  readonly error = signal('');

  private readonly products = signal<Product[]>([]);
  private readonly stocks = signal<StockItem[]>([]);

  readonly options = computed((): ProductOption[] => {
    const stockMap = new Map(this.stocks().map((s) => [s.productId, s.quantity]));
    return this.products().map((p) => ({
      productId: p.productId,
      name: p.name,
      quantity: stockMap.get(p.productId) ?? 0,
    }));
  });

  ngOnInit(): void {
    this.productService.getProducts().subscribe({ next: (p) => this.products.set(p) });
    this.stockService.getStocks().subscribe({ next: (s) => this.stocks.set(s) });
  }

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
    if (items.some((i) => !i.productId)) {
      this.error.set('Wybierz produkt dla każdej pozycji');
      return;
    }

    const productIds = items.map((i) => i.productId);
    if (new Set(productIds).size !== productIds.length) {
      this.error.set('Ten sam produkt nie może wystąpić na dwóch pozycjach');
      return;
    }

    this.error.set('');
    this.loading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.orderService.createOrder({ userId, items }).subscribe({ next: () => resolve(), error: reject });
      });
      await this.router.navigate(['/orders']);
    } catch {
      this.error.set('Nie udało się złożyć zamówienia');
    } finally {
      this.loading.set(false);
    }
  }
}
