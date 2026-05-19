import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import type { Role } from '../../types';

export const roleGuard = (...roles: Role[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (auth.isAuthenticated() && auth.hasAnyRole(...roles)) return true;
    return router.createUrlTree(['/dashboard']);
  };