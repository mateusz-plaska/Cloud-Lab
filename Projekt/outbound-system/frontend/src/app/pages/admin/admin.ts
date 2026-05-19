import { Component, OnInit, inject, signal } from '@angular/core';
import { AdminService } from '../../core/services/admin.service';
import type { UserDto } from '../../types';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [],
  templateUrl: './admin.html',
})
export class Admin implements OnInit {
  private readonly adminService = inject(AdminService);

  readonly users = signal<UserDto[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

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

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString('pl-PL');
  }
}