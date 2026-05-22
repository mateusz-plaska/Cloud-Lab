export type Role = 'USER' | 'OPERATOR' | 'ADMIN';

export interface Product {
  productId: string;
  name: string;
}

export type OrderStatus = 'PLANNED' | 'IN_PROGRESS' | 'PACKED' | 'READY' | 'COMPLETED' | 'FAILED';

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

export type SseEventType =
  | 'ORDER_CREATED'
  | 'STOCK_RESERVED'
  | 'ALLOCATION_FAILED'
  | 'ORDER_PICKED'
  | 'PICK_FAILED'
  | 'PACKING_FINISHED'
  | 'SHIPMENT_CREATED';

export interface OrderStatusUpdate {
  orderId: string;
  eventType: SseEventType;
  station: string;
  timestamp: string;
}

export interface Shipment {
  orderId: string;
  trackingNumber: string;
  shippingCost: number;
  shippedAt: string;
}

export interface UserDto {
  id: string;
  username: string;
  email: string;
  role: string;
  createdAt: string;
}