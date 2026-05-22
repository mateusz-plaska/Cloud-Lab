import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import type { Shipment } from '../../types';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private readonly http = inject(HttpClient);

  getShipment(orderId: string): Observable<Shipment> {
    return this.http.get<Shipment>(`${API_URL}/api/shipments/${orderId}`);
  }
}
