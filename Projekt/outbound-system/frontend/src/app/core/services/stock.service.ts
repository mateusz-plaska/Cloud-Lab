import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { StockItem } from '../../types';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly http = inject(HttpClient);

  getStocks() {
    return this.http.get<StockItem[]>(`${API_URL}/api/stocks`);
  }

  addStock(productId: string, quantity: number) {
    return this.http.post<void>(`${API_URL}/api/stocks`, { productId, quantity });
  }
}
