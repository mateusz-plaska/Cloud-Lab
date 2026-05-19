import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../api';
import type { UserDto } from '../../types';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);

  getUsers() {
    return this.http.get<UserDto[]>(`${API_URL}/api/admin/users`);
  }
}