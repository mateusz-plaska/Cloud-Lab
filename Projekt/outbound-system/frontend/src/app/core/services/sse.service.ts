import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import { AuthService } from './auth.service';
import type { OrderStatusUpdate } from '../../types';

@Injectable({ providedIn: 'root' })
export class SseService {
  private readonly auth = inject(AuthService);

  watch(orderId: string): Observable<OrderStatusUpdate> {
    return new Observable((observer) => {
      const token = this.auth.token();
      const url = `${API_URL}/api/sse/orders/${orderId}?token=${token}`;
      const source = new EventSource(url);

      source.addEventListener('order-update', (event: MessageEvent) => {
        try {
          observer.next(JSON.parse(event.data) as OrderStatusUpdate);
        } catch {
          // ignore malformed events
        }
      });

      source.onerror = () => observer.error('SSE connection error');

      return () => source.close();
    });
  }
}