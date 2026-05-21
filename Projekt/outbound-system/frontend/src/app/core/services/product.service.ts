import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { Product } from '../../types';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);

  getProducts() {
    return this.http.get<Product[]>(`${API_URL}/api/products`);
  }

  createProduct(name: string) {
    return this.http.post<Product>(`${API_URL}/api/products`, { name });
  }
}