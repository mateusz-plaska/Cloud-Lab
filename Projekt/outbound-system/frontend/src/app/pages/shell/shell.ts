import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import type { Role } from '../../types';

interface NavItem {
  label: string;
  route: string;
  roles?: Role[];
}

const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', route: '/dashboard' },
  { label: 'Zamówienia', route: '/orders' },
  { label: 'Nowe zamówienie', route: '/orders/new', roles: ['USER'] },
  { label: 'Picking', route: '/picking', roles: ['OPERATOR', 'ADMIN'] },
  { label: 'Packing', route: '/packing', roles: ['OPERATOR', 'ADMIN'] },
  { label: 'Magazyn', route: '/stocks', roles: ['OPERATOR', 'ADMIN'] },
  { label: 'Użytkownicy', route: '/admin', roles: ['ADMIN'] },
];

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly auth = inject(AuthService);

  protected get navItems(): NavItem[] {
    return NAV_ITEMS.filter(
      (item) => !item.roles || item.roles.some((r) => this.auth.hasRole(r)),
    );
  }

  protected logout(): void {
    this.auth.logout();
  }
}