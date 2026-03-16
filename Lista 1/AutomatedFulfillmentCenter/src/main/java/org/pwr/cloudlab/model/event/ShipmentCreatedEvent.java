package org.pwr.cloudlab.model.event;

import java.util.UUID;

public record ShipmentCreatedEvent(String shipmentId, String orderId, String customerId, Long createdAt) {

    public static ShipmentCreatedEvent from(String orderId, String customerId) {
        var trackingNumber = "TRK-" + UUID.randomUUID();
        return new ShipmentCreatedEvent(trackingNumber, orderId, customerId, System.currentTimeMillis());
    }
}
