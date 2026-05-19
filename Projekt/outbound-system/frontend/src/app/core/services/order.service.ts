import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { CreateOrderRequest, OrderListItem, OrderReport } from '../../types';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  getOrders() {
    return this.http.get<OrderListItem[]>(`${API_URL}/api/orders`);
  }

  getOrderReport(orderId: string) {
    return this.http.get<OrderReport>(`${API_URL}/api/orders/${orderId}`);
  }

  createOrder(request: CreateOrderRequest) {
    return this.http.post<string>(`${API_URL}/api/orders`, request);
  }
}