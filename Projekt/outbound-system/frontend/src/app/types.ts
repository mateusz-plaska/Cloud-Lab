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

export interface SsoConfig {
  enabled: boolean;
  authorizationUri: string;
  clientId: string;
  redirectUri: string;
  scopes: string;
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
  role: Role;
  createdAt: string;
}

export interface OrderProduct {
  productId: string;
  name: string;
  quantity: number;
}

export interface StockItem {
  productId: string;
  quantity: number;
}

export interface ThroughputPoint {
  timestamp: number;
  counts: Partial<Record<SseEventType, number>>;
}

export interface ThroughputResponse {
  bucketMs: number;
  points: ThroughputPoint[];
}

export interface LeadTimePoint {
  timestamp: number;
  completedOrders: number;
  avgStageSeconds: Record<string, number | null>;
  p95TotalSeconds: number;
}

export interface LeadTimeResponse {
  bucketMs: number;
  points: LeadTimePoint[];
}
