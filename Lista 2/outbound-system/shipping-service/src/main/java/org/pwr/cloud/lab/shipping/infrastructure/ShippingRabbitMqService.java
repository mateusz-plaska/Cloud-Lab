package org.pwr.cloud.lab.shipping.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.ShipmentCreatedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;
import org.pwr.cloud.lab.common.messagebroker.EventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ShippingRabbitMqService {
    private final EventPublisher eventPublisher;

    public void sendShipmentCreatedEvent(OrderId orderId, TrackingNumber trackingNumber, Instant shippedAt) {
        eventPublisher.publish(new ShipmentCreatedEvent(orderId, trackingNumber, shippedAt));
    }
}
