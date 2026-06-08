import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { DashboardStats, LeadTimeResponse, ThroughputResponse } from '../../types';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  getStats() {
    return this.http.get<DashboardStats>(`${API_URL}/api/dashboard`);
  }

  getThroughput(fromMs: number, toMs: number, zone: string, bucketMs?: number) {
    return this.http.get<ThroughputResponse>(
      `${API_URL}/api/dashboard/throughput${this.query(fromMs, toMs, zone, bucketMs)}`,
    );
  }

  getLeadTime(fromMs: number, toMs: number, zone: string, bucketMs?: number) {
    return this.http.get<LeadTimeResponse>(
      `${API_URL}/api/dashboard/lead-time${this.query(fromMs, toMs, zone, bucketMs)}`,
    );
  }

  private query(fromMs: number, toMs: number, zone: string, bucketMs?: number): string {
    const params = new URLSearchParams({ fromMs: String(fromMs), toMs: String(toMs), zone });
    if (bucketMs != null) params.set('bucketMs', String(bucketMs));
    return `?${params.toString()}`;
  }
}
