import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../core/services/admin.service';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import { SseService } from '../../core/services/sse.service';
import type { OrderListItem, OrderStatus, UserDto } from '../../types';
import { PaginationComponent } from '../../shared/pagination';

export const STATUS_CLASSES: Record<OrderStatus, string> = {
  PLANNED: 'bg-yellow-100 text-yellow-800',
  IN_PROGRESS: 'bg-blue-100 text-blue-800',
  PACKED: 'bg-orange-100 text-orange-800',
  READY: 'bg-purple-100 text-purple-800',
  COMPLETED: 'bg-green-100 text-green-800',
  FAILED: 'bg-red-100 text-red-800',
};

const EVENT_TO_STATUS: Partial<Record<string, OrderStatus>> = {
  StockReservedEvent: 'IN_PROGRESS',
  AllocationFailedEvent: 'FAILED',
  OrderPickedEvent: 'COMPLETED',
  OrderPickFailedEvent: 'FAILED',
  PackingFinishedEvent: 'PACKED',
  ShipmentCreatedEvent: 'READY',
};

export const ALL_STATUSES: (OrderStatus | '')[] = [
  '',
  'PLANNED',
  'IN_PROGRESS',
  'PACKED',
  'READY',
  'COMPLETED',
  'FAILED',
];

const PAGE_SIZE = 10;

type SortField = 'orderId' | 'customerId' | 'status' | 'itemCount' | 'createdAt';
type SortDir = 'asc' | 'desc';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [RouterLink, FormsModule, NgSwitch, NgSwitchCase, NgSwitchDefault, PaginationComponent],
  templateUrl: './orders.html',
})
export class Orders implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly orderService = inject(OrderService);
  private readonly adminService = inject(AdminService);
  private readonly sseService = inject(SseService);
  private readonly destroyRef = inject(DestroyRef);

  readonly orders = signal<OrderListItem[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  private readonly users = signal<UserDto[]>([]);
  private readonly userMap = computed(() => new Map(this.users().map((u) => [u.id, u.username])));

  readonly search = signal('');
  readonly statusFilter = signal<OrderStatus | string>('');
  readonly sortField = signal<SortField | null>(null);
  readonly sortDir = signal<SortDir>('asc');

  readonly statuses = ALL_STATUSES;

  readonly columns: { field: SortField; label: string }[] = [
    { field: 'orderId', label: 'ID zamówienia' },
    ...(this.auth.isOperatorOrAdmin() ? [{ field: 'customerId' as SortField, label: 'Klient' }] : []),
    { field: 'status', label: 'Status' },
    { field: 'itemCount', label: 'Poz.' },
    { field: 'createdAt', label: 'Utworzono' },
  ];

  readonly page = signal(1);

  readonly filtered = computed(() => {
    const q = this.search().toLowerCase();
    const status = this.statusFilter();
    const field = this.sortField();
    const dir = this.sortDir();

    const userMap = this.userMap();
    let list = this.orders().filter((o) => {
      const username = userMap.get(o.customerId) ?? '';
      const matchSearch =
        !q ||
        o.orderId.toLowerCase().includes(q) ||
        o.customerId.toLowerCase().includes(q) ||
        username.toLowerCase().includes(q);
      const matchStatus = !status || o.status === status;
      return matchSearch && matchStatus;
    });

    if (field) {
      list = [...list].sort((a, b) => {
        const av = field === 'itemCount' ? a[field] : String(a[field]);
        const bv = field === 'itemCount' ? b[field] : String(b[field]);
        const cmp = av < bv ? -1 : av > bv ? 1 : 0;
        return dir === 'asc' ? cmp : -cmp;
      });
    }

    return list;
  });

  readonly totalPages = computed(() => Math.max(1, Math.ceil(this.filtered().length / PAGE_SIZE)));

  readonly paginated = computed(() => {
    const p = this.page();
    const list = this.filtered();
    const safe = Math.min(p, Math.max(1, Math.ceil(list.length / PAGE_SIZE)));
    return list.slice((safe - 1) * PAGE_SIZE, safe * PAGE_SIZE);
  });

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

    if (this.auth.hasAnyRole('OPERATOR', 'ADMIN')) {
      this.adminService.getUsers().subscribe({ next: (u) => this.users.set(u) });
    }

    this.sseService
      .watchDashboard()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (update) => {
          const newStatus = EVENT_TO_STATUS[update.eventType];
          if (!newStatus) return;
          this.orders.update((list) =>
            list.map((o) => (o.orderId === update.orderId ? { ...o, status: newStatus } : o)),
          );
        },
      });
  }

  customerName(customerId: string): string | null {
    return this.userMap().get(customerId) ?? null;
  }

  onSearch(value: string): void { this.search.set(value); this.page.set(1); }
  onStatusFilter(value: string): void { this.statusFilter.set(value); this.page.set(1); }

  sort(field: SortField): void {
    if (this.sortField() !== field) {
      this.sortField.set(field);
      this.sortDir.set('asc');
    } else if (this.sortDir() === 'asc') {
      this.sortDir.set('desc');
    } else {
      this.sortField.set(null);
    }
  }

  sortState(field: SortField): 'none' | 'asc' | 'desc' {
    if (this.sortField() !== field) return 'none';
    return this.sortDir();
  }

  statusClass(status: OrderStatus): string {
    return STATUS_CLASSES[status] ?? 'bg-slate-100 text-slate-800';
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString('pl-PL', {
      timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
    });
  }
}
