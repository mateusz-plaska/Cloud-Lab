package org.pwr.cloud.lab.shipping.domain.messaging;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;

import java.time.Instant;

public interface ShippingEventPublisher {
    void publishShipmentCreated(OrderId orderId, TrackingNumber trackingNumber, Instant shippedAt);
}
