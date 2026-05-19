import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { DashboardStats } from '../../types';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  getStats() {
    return this.http.get<DashboardStats>(`${API_URL}/api/dashboard`);
  }
}