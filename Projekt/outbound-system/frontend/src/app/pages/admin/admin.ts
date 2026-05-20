import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';
import type { Role, UserDto } from '../../types';
import { PaginationComponent } from '../../shared/pagination';

type SortField = 'username' | 'email' | 'role' | 'createdAt';
type SortDir = 'asc' | 'desc';

const PAGE_SIZE = 10;

const ROLE_CLASSES: Record<Role, string> = {
  ADMIN: 'bg-red-100 text-red-800',
  OPERATOR: 'bg-indigo-100 text-indigo-800',
  USER: 'bg-slate-100 text-slate-800',
};

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule, NgSwitch, NgSwitchCase, NgSwitchDefault, PaginationComponent],
  templateUrl: './admin.html',
})
export class Admin implements OnInit {
  private readonly adminService = inject(AdminService);

  readonly users = signal<UserDto[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly search = signal('');
  readonly sortField = signal<SortField | null>('createdAt');
  readonly sortDir = signal<SortDir>('desc');
  readonly page = signal(1);

  readonly filtered = computed(() => {
    const q = this.search().toLowerCase();
    const field = this.sortField();
    const dir = this.sortDir();

    let list = this.users().filter((u) =>
      !q ||
      u.username.toLowerCase().includes(q) ||
      u.email.toLowerCase().includes(q) ||
      u.role.toLowerCase().includes(q),
    );

    if (field) {
      list = [...list].sort((a, b) => {
        const av = String(a[field]);
        const bv = String(b[field]);
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

  newUsername = '';
  newEmail = '';
  newPassword = '';
  newRole: Role = 'OPERATOR';
  readonly createLoading = signal(false);
  readonly createMsg = signal('');
  readonly createError = signal('');

  readonly roles: Role[] = ['USER', 'OPERATOR', 'ADMIN'];

  ngOnInit(): void {
    this.adminService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Nie udało się pobrać listy użytkowników');
        this.loading.set(false);
      },
    });
  }

  async createUser(): Promise<void> {
    if (this.createLoading()) return;
    this.createMsg.set('');
    this.createError.set('');
    this.createLoading.set(true);
    try {
      await new Promise<UserDto>((res, rej) =>
        this.adminService.createUser({
          username: this.newUsername.trim(),
          email: this.newEmail.trim(),
          password: this.newPassword,
          role: this.newRole,
        }).subscribe({ next: res, error: rej }),
      ).then((created) => {
        this.users.update((list) => [created, ...list]);
        this.createMsg.set(`Konto „${created.username}" (${created.role}) zostało utworzone`);
        this.newUsername = '';
        this.newEmail = '';
        this.newPassword = '';
        this.newRole = 'OPERATOR';
      });
    } catch {
      this.createError.set('Nie udało się utworzyć konta');
    } finally {
      this.createLoading.set(false);
    }
  }

  onSearch(value: string): void { this.search.set(value); this.page.set(1); }

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

  roleClass(role: Role): string {
    return ROLE_CLASSES[role] ?? 'bg-slate-100 text-slate-800';
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString('pl-PL');
  }
}