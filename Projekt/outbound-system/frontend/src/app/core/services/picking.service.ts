import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../api';

@Injectable({ providedIn: 'root' })
export class PickingService {
  private readonly http = inject(HttpClient);

  pickItem(orderId: string, productId: string, quantity: number) {
    const params = new HttpParams().set('productId', productId).set('quantity', quantity);
    return this.http.post<string>(`${API_URL}/api/picking/${orderId}/pick`, null, { params });
  }

  failItem(orderId: string, productId: string, reason: string) {
    const params = new HttpParams().set('productId', productId).set('reason', reason);
    return this.http.post<string>(`${API_URL}/api/picking/${orderId}/fail`, null, { params });
  }
}