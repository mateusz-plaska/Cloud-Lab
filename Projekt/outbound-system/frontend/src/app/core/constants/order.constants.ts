import type { OrderStatus, SseEventType } from '../../types';

export const STATUS_CLASSES: Record<OrderStatus, string> = {
  PLANNED: 'bg-blue-100 text-blue-800',
  IN_PROGRESS: 'bg-cyan-100 text-cyan-800',
  PACKED: 'bg-orange-100 text-orange-800',
  READY: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-purple-100 text-purple-800',
  FAILED: 'bg-red-100 text-red-800',
};

export const ALL_STATUSES: (OrderStatus | '')[] = [
  '',
  'PLANNED',
  'IN_PROGRESS',
  'PACKED',
  'READY',
  'COMPLETED',
  'FAILED',
];

export const EVENT_TO_STATUS: Partial<Record<SseEventType, OrderStatus>> = {
  ORDER_CREATED: 'PLANNED',
  STOCK_RESERVED: 'IN_PROGRESS',
  ALLOCATION_FAILED: 'FAILED',
  ORDER_PICKED: 'COMPLETED',
  PICK_FAILED: 'FAILED',
  PACKING_FINISHED: 'PACKED',
  SHIPMENT_CREATED: 'READY',
};

export const EVENT_LABEL: Record<SseEventType, string> = {
  ORDER_CREATED: 'Zamówienie przyjęte',
  STOCK_RESERVED: 'Towar zarezerwowany',
  ALLOCATION_FAILED: 'Błąd rezerwacji towaru',
  ORDER_PICKED: 'Kompletacja zakończona',
  PICK_FAILED: 'Błąd kompletacji',
  PACKING_FINISHED: 'Zamówienie zapakowane',
  SHIPMENT_CREATED: 'Przesyłka nadana',
};

export const STATION_LABEL: Record<string, string> = {
  'order-gateway': 'System zamówień',
  reservation: 'Magazyn',
  picking: 'Picking',
  packing: 'Packing',
  shipping: 'Wysyłka',
};

export const ERROR_EVENTS = new Set<SseEventType>(['ALLOCATION_FAILED', 'PICK_FAILED']);

export const PAGE_SIZE = 10;
