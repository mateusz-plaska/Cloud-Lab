import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../api';
import type { AuthResponse, AuthUser, LoginRequest, RegisterRequest, Role } from '../../types';
import { ConfigService } from './config.service';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly config = inject(ConfigService);

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly _user = signal<AuthUser | null>(this.loadUser());

  readonly token = this._token.asReadonly();
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._token() !== null);

  private refreshTimer: ReturnType<typeof setTimeout> | null = null;
  private lastActivityMs = Date.now();

  constructor() {
    (['mousemove', 'keydown', 'click', 'touchstart'] as const).forEach((e) =>
      document.addEventListener(
        e,
        () => {
          this.lastActivityMs = Date.now();
        },
        { passive: true },
      ),
    );
    if (this.isAuthenticated()) {
      const expMs = this.getTokenExpiryMs();
      if (!expMs || expMs <= Date.now() + this.config.refreshBeforeExpiryMs) {
        this.clearSession();
      } else {
        this.scheduleRefresh();
      }
    }
  }

  async login(request: LoginRequest): Promise<void> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(`${API_URL}/auth/login`, request),
    );
    this.saveSession(response);
  }

  async register(request: RegisterRequest): Promise<void> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(`${API_URL}/auth/register`, request),
    );
    this.saveSession(response);
  }

  async exchangeSso(code: string, codeVerifier: string): Promise<void> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(`${API_URL}/auth/sso/exchange`, { code, codeVerifier }),
    );
    this.saveSession(response);
  }

  logout(): void {
    this.clearSession();
    void this.router.navigate(['/login']);
  }

  hasRole(role: Role): boolean {
    return this._user()?.role === role;
  }

  hasAnyRole(...roles: Role[]): boolean {
    return roles.some((r) => this.hasRole(r));
  }

  isOperatorOrAdmin(): boolean {
    return this.hasAnyRole('OPERATOR', 'ADMIN');
  }

  private saveSession(response: AuthResponse): void {
    const user: AuthUser = {
      userId: response.userId,
      username: response.username,
      role: response.role as Role,
    };
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this._token.set(response.token);
    this._user.set(user);
    this.scheduleRefresh();
  }

  private clearSession(): void {
    if (this.refreshTimer !== null) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = null;
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._token.set(null);
    this._user.set(null);
  }

  private scheduleRefresh(): void {
    if (this.refreshTimer !== null) clearTimeout(this.refreshTimer);
    const expMs = this.getTokenExpiryMs();
    if (!expMs) return;
    const delay = expMs - Date.now() - this.config.refreshBeforeExpiryMs;
    if (delay <= 0) return;
    this.refreshTimer = setTimeout(() => this.attemptRefresh(), delay);
  }

  private async attemptRefresh(): Promise<void> {
    if (Date.now() - this.lastActivityMs > this.config.inactivityLimitMs) {
      this.logout();
      return;
    }
    try {
      const response = await firstValueFrom(
        this.http.post<AuthResponse>(`${API_URL}/auth/refresh`, {}),
      );
      this.saveSession(response);
    } catch {
      this.logout();
    }
  }

  private getTokenExpiryMs(): number | null {
    const token = this._token();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp ? (payload.exp as number) * 1000 : null;
    } catch {
      return null;
    }
  }

  private loadUser(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  }
}
