package org.pwr.cloud.lab.bff.api.dto.sse;

public enum SseEventType {
    ORDER_CREATED,
    STOCK_RESERVED,
    ALLOCATION_FAILED,
    ORDER_PICKED,
    PICK_FAILED,
    PACKING_FINISHED,
    SHIPMENT_CREATED
}
