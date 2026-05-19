import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { FinishPackingRequest } from '../../types';

@Injectable({ providedIn: 'root' })
export class PackingService {
  private readonly http = inject(HttpClient);

  finishPacking(orderId: string, request: FinishPackingRequest) {
    return this.http.post<string>(`${API_URL}/api/packing/${orderId}`, request);
  }
}