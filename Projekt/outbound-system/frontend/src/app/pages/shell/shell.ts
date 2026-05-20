import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import type { Role } from '../../types';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles?: Role[];
}

const NAV_ITEMS: NavItem[] = [
  {
    label: 'Dashboard', route: '/dashboard',
    icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
  },
  {
    label: 'Zamówienia', route: '/orders',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01',
  },
  {
    label: 'Nowe zamówienie', route: '/orders/new', roles: ['USER'],
    icon: 'M12 4v16m8-8H4',
  },
  {
    label: 'Picking', route: '/picking', roles: ['OPERATOR', 'ADMIN'],
    icon: 'M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z',
  },
  {
    label: 'Packing', route: '/packing', roles: ['OPERATOR', 'ADMIN'],
    icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
  },
  {
    label: 'Magazyn', route: '/stocks', roles: ['OPERATOR', 'ADMIN'],
    icon: 'M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z',
  },
  {
    label: 'Użytkownicy', route: '/admin', roles: ['ADMIN'],
    icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z',
  },
];

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly auth = inject(AuthService);
  protected readonly collapsed = signal(localStorage.getItem('sidebar-collapsed') === 'true');

  protected get navItems(): NavItem[] {
    return NAV_ITEMS.filter(
      (item) => !item.roles || item.roles.some((r) => this.auth.hasRole(r)),
    );
  }

  protected toggle(): void {
    this.collapsed.update((v) => !v);
    localStorage.setItem('sidebar-collapsed', String(this.collapsed()));
  }

  protected logout(): void {
    this.auth.logout();
  }
}
