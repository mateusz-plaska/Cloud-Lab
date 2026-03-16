package org.pwr.cloudlab.model.event;

import java.util.UUID;

public record OrderPackedEvent(String orderId, String customerId, Long packedAt) {

    public static OrderPackedEvent from() {
        var orderId = "ORD-" + UUID.randomUUID();
        var customerId = "CUST-" + UUID.randomUUID();
        return new OrderPackedEvent(orderId, customerId, System.currentTimeMillis());
    }
}
