import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
  },
  {
    path: '',
    loadComponent: () => import('./pages/shell/shell').then((m) => m.Shell),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'orders',
        loadComponent: () => import('./pages/orders/orders').then((m) => m.Orders),
      },
      {
        path: 'orders/new',
        loadComponent: () =>
          import('./pages/order-create/order-create').then((m) => m.OrderCreate),
        canActivate: [roleGuard('USER')],
      },
      {
        path: 'orders/:id',
        loadComponent: () =>
          import('./pages/order-detail/order-detail').then((m) => m.OrderDetail),
      },
      {
        path: 'picking',
        loadComponent: () => import('./pages/picking/picking').then((m) => m.Picking),
        canActivate: [roleGuard('OPERATOR', 'ADMIN')],
      },
      {
        path: 'packing',
        loadComponent: () => import('./pages/packing/packing').then((m) => m.Packing),
        canActivate: [roleGuard('OPERATOR', 'ADMIN')],
      },
      {
        path: 'stocks',
        loadComponent: () => import('./pages/stocks/stocks').then((m) => m.Stocks),
        canActivate: [roleGuard('OPERATOR', 'ADMIN')],
      },
      {
        path: 'admin',
        loadComponent: () => import('./pages/admin/admin').then((m) => m.Admin),
        canActivate: [roleGuard('ADMIN')],
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '/login' },
];