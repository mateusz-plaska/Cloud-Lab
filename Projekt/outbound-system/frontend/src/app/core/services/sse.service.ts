import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import { AuthService } from './auth.service';
import type { OrderStatusUpdate } from '../../types';

@Injectable({ providedIn: 'root' })
export class SseService {
  private readonly auth = inject(AuthService);

  watch(orderId: string): Observable<OrderStatusUpdate> {
    return this.connect<OrderStatusUpdate>(`${API_URL}/api/sse/orders/${orderId}`, 'order-update');
  }

  watchDashboard(): Observable<OrderStatusUpdate> {
    return this.connect<OrderStatusUpdate>(`${API_URL}/api/sse/dashboard`, 'dashboard-update');
  }

  private connect<T>(path: string, eventName: string): Observable<T> {
    return new Observable((observer) => {
      const token = this.auth.token();
      const source = new EventSource(`${path}?token=${token}`);

      source.addEventListener(eventName, (event: MessageEvent) => {
        try {
          observer.next(JSON.parse(event.data) as T);
        } catch {
          // ignore malformed events
        }
      });

      source.onerror = () => observer.error('SSE connection error');

      return () => source.close();
    });
  }
}