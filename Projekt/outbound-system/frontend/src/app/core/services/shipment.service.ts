import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private readonly http = inject(HttpClient);

  getShipment(orderId: string) {
    return this.http.get<string>(`${API_URL}/api/shipments/${orderId}`);
  }
}