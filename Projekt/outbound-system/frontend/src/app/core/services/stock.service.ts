import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../api';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly http = inject(HttpClient);

  addStock(productId: string, quantity: number) {
    const params = new HttpParams().set('productId', productId).set('quantity', quantity);
    return this.http.post<void>(`${API_URL}/api/stocks`, null, { params });
  }
}