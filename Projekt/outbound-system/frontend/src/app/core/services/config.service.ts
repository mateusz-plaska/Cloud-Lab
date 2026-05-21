import { Injectable, inject } from '@angular/core';
import { HttpBackend, HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../api';

interface JwtConfig {
  refreshBeforeExpiryMs: number;
  inactivityLimitMs: number;
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private readonly http = new HttpClient(inject(HttpBackend));

  refreshBeforeExpiryMs = 5 * 60 * 1000;   // fallback: 5m
  inactivityLimitMs = 2 * 60 * 60 * 1000;  // fallback: 2h

  async load(): Promise<void> {
    try {
      const config = await firstValueFrom(
        this.http.get<JwtConfig>(`${API_URL}/auth/config`),
      );
      this.refreshBeforeExpiryMs = config.refreshBeforeExpiryMs;
      this.inactivityLimitMs = config.inactivityLimitMs;
    } catch {
      // use fallback defaults
    }
  }
}
