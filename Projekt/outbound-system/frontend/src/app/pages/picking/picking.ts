import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { PickingService } from '../../core/services/picking.service';
import { ProductService } from '../../core/services/product.service';
import type { OrderListItem, OrderReport, Product } from '../../types';
import { PaginationComponent } from '../../shared/pagination';

const PAGE_SIZE = 10;

interface OrderProduct {
  productId: string;
  name: string;
  quantity: number;
}

@Component({
  selector: 'app-picking',
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './picking.html',
})
export class Picking implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly pickingService = inject(PickingService);
  private readonly productService = inject(ProductService);

  readonly orders = signal<OrderListItem[]>([]);
  readonly ordersLoading = signal(true);
  readonly search = signal('');

  readonly selectedOrder = signal<OrderListItem | null>(null);
  readonly orderReport = signal<OrderReport | null>(null);
  readonly reportLoading = signal(false);

  private readonly products = signal<Product[]>([]);
  private readonly processedIds = signal<string[]>([]);

  readonly selectedProduct = signal<OrderProduct | null>(null);
  pickQty = 1;
  failReason = '';
  readonly actionLoading = signal(false);
  readonly actionMsg = signal<{ ok: boolean; text: string } | null>(null);

  private readonly productMap = computed(() => new Map(this.products().map((p) => [p.productId, p.name])));

  readonly filteredOrders = computed(() => {
    const q = this.search().toLowerCase();
    return !q ? this.orders() : this.orders().filter((o) => o.orderId.toLowerCase().includes(q));
  });

  readonly page = signal(1);
  readonly totalOrderPages = computed(() => Math.max(1, Math.ceil(this.filteredOrders().length / PAGE_SIZE)));
  readonly paginatedOrders = computed(() => {
    const p = this.page();
    const list = this.filteredOrders();
    const safe = Math.min(p, Math.max(1, Math.ceil(list.length / PAGE_SIZE)));
    return list.slice((safe - 1) * PAGE_SIZE, safe * PAGE_SIZE);
  });

  readonly orderProducts = computed((): OrderProduct[] => {
    const report = this.orderReport();
    const map = this.productMap();
    if (!report) return [];
    return report.products
      .map((s) => {
        const m = s.match(/^(.+) \(x(\d+)\)$/);
        if (!m) return null;
        const productId = m[1];
        return { productId, name: map.get(productId) ?? productId.slice(0, 8) + '…', quantity: parseInt(m[2], 10) };
      })
      .filter((p): p is OrderProduct => p !== null);
  });

  readonly remainingProducts = computed(() => {
    const done = new Set(this.processedIds());
    return this.orderProducts().filter((p) => !done.has(p.productId));
  });

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders.filter((o) => o.status === 'IN_PROGRESS'));
        this.ordersLoading.set(false);
      },
      error: () => this.ordersLoading.set(false),
    });
    this.productService.getProducts().subscribe({ next: (p) => this.products.set(p) });
  }

  onSearch(value: string): void { this.search.set(value); this.page.set(1); }

  selectOrder(order: OrderListItem): void {
    if (this.selectedOrder()?.orderId === order.orderId) return;
    this.selectedOrder.set(order);
    this.selectedProduct.set(null);
    this.processedIds.set([]);
    this.actionMsg.set(null);
    this.reportLoading.set(true);
    this.orderReport.set(null);
    this.orderService.getOrderReport(order.orderId).subscribe({
      next: (r) => { this.orderReport.set(r); this.reportLoading.set(false); },
      error: () => this.reportLoading.set(false),
    });
  }

  selectProduct(product: OrderProduct): void {
    this.selectedProduct.set(product);
    this.pickQty = product.quantity;
    this.failReason = '';
    this.actionMsg.set(null);
  }

  async pick(): Promise<void> {
    const order = this.selectedOrder();
    const product = this.selectedProduct();
    if (!order || !product || this.actionLoading()) return;
    this.actionMsg.set(null);
    this.actionLoading.set(true);
    try {
      await new Promise<void>((res, rej) =>
        this.pickingService.pickItem(order.orderId, product.productId, this.pickQty).subscribe({ next: () => res(), error: rej }),
      );
      this.actionMsg.set({ ok: true, text: `„${product.name}" spickowany` });
      this.markProcessed(order, product.productId);
    } catch {
      this.actionMsg.set({ ok: false, text: 'Operacja nie powiodła się' });
    } finally {
      this.actionLoading.set(false);
    }
  }

  async fail(): Promise<void> {
    const order = this.selectedOrder();
    const product = this.selectedProduct();
    if (!order || !product || !this.failReason.trim() || this.actionLoading()) return;
    this.actionMsg.set(null);
    this.actionLoading.set(true);
    try {
      await new Promise<void>((res, rej) =>
        this.pickingService.failItem(order.orderId, product.productId, this.failReason).subscribe({ next: () => res(), error: rej }),
      );
      this.actionMsg.set({ ok: true, text: `„${product.name}" oznaczony jako nieudany` });
      this.markProcessed(order, product.productId);
    } catch {
      this.actionMsg.set({ ok: false, text: 'Operacja nie powiodła się' });
    } finally {
      this.actionLoading.set(false);
    }
  }

  private markProcessed(order: OrderListItem, productId: string): void {
    this.processedIds.update((ids) => [...ids, productId]);
    this.selectedProduct.set(null);
    if (this.remainingProducts().length === 0) {
      this.orders.update((list) => list.filter((o) => o.orderId !== order.orderId));
      this.selectedOrder.set(null);
      this.orderReport.set(null);
      this.processedIds.set([]);
    }
  }
}
