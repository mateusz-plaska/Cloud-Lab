export type Role = 'USER' | 'OPERATOR' | 'ADMIN';

export type OrderStatus = 'PENDING' | 'RESERVED' | 'PICKED' | 'PACKED' | 'SHIPPED' | 'FAILED';

export type BoxSize = 'SMALL' | 'MEDIUM' | 'LARGE' | 'EXTRA_LARGE';

export interface AuthUser {
  userId: string;
  username: string;
  role: Role;
}

export interface AuthResponse {
  token: string;
  userId: string;
  username: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface OrderItem {
  productId: string;
  quantity: number;
}

export interface OrderListItem {
  orderId: string;
  customerId: string;
  status: OrderStatus;
  itemCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  userId: string;
  items: OrderItem[];
}

export interface OrderReport {
  orderId: string;
  status: OrderStatus;
  products: string[];
  details: Record<string, string>;
  updatedAt: string;
}

export interface DashboardStats {
  totalOrders: number;
  byStatus: Record<string, number>;
}

export interface FinishPackingRequest {
  boxSize: BoxSize;
  weight: number;
}

export interface OrderStatusUpdate {
  orderId: string;
  status: OrderStatus;
  timestamp: string;
}

export interface UserDto {
  id: string;
  username: string;
  email: string;
  role: string;
  createdAt: string;
}