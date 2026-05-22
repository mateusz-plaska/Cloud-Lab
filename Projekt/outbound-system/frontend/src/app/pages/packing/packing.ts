import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { PackingService } from '../../core/services/packing.service';
import { ProductService } from '../../core/services/product.service';
import { parseProducts } from '../../core/utils/product-parser';
import { PAGE_SIZE } from '../../core/constants/order.constants';
import type { BoxSize, OrderListItem, OrderProduct, OrderReport, Product } from '../../types';
import { PaginationComponent } from '../../shared/pagination';

const BOX_SIZES: BoxSize[] = ['SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE'];

@Component({
  selector: 'app-packing',
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './packing.html',
})
export class Packing implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly packingService = inject(PackingService);
  private readonly productService = inject(ProductService);

  readonly orders = signal<OrderListItem[]>([]);
  readonly ordersLoading = signal(true);
  readonly search = signal('');

  readonly selectedOrder = signal<OrderListItem | null>(null);
  readonly orderReport = signal<OrderReport | null>(null);
  readonly reportLoading = signal(false);

  private readonly products = signal<Product[]>([]);
  private readonly productMap = computed(() => new Map(this.products().map((p) => [p.productId, p.name])));

  readonly orderProducts = computed((): OrderProduct[] => {
    const report = this.orderReport();
    if (!report) return [];
    return parseProducts(report.products, this.productMap());
  });

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

  readonly boxSizes = BOX_SIZES;
  boxSize: BoxSize = 'MEDIUM';
  weight = 1.0;
  readonly actionLoading = signal(false);
  readonly successMsg = signal('');
  readonly errorMsg = signal('');

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders.filter((o) => o.status === 'COMPLETED'));
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
    this.successMsg.set('');
    this.errorMsg.set('');
    this.reportLoading.set(true);
    this.orderReport.set(null);
    this.orderService.getOrderReport(order.orderId).subscribe({
      next: (r) => { this.orderReport.set(r); this.reportLoading.set(false); },
      error: () => this.reportLoading.set(false),
    });
  }

  async pack(): Promise<void> {
    const order = this.selectedOrder();
    if (!order || this.actionLoading()) return;
    this.successMsg.set('');
    this.errorMsg.set('');
    this.actionLoading.set(true);
    try {
      await new Promise<void>((res, rej) =>
        this.packingService.finishPacking(order.orderId, { boxSize: this.boxSize, weight: this.weight })
          .subscribe({ next: () => res(), error: rej }),
      );
      this.successMsg.set('Pakowanie zakończone');
      this.orders.update((list) => list.filter((o) => o.orderId !== order.orderId));
      this.selectedOrder.set(null);
      this.orderReport.set(null);
    } catch {
      this.errorMsg.set('Operacja nie powiodła się');
    } finally {
      this.actionLoading.set(false);
    }
  }
}
