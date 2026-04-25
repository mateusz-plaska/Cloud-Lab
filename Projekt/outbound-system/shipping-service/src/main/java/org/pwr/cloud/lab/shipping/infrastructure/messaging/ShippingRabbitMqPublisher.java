package org.pwr.cloud.lab.shipping.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.ShipmentCreatedEvent;
import org.pwr.cloud.lab.common.domain.messaging.EventPublisher;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.messaging.ShippingEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ShippingRabbitMqPublisher implements ShippingEventPublisher {
    private final EventPublisher eventPublisher;

    @Override
    public void publishShipmentCreated(OrderId orderId, TrackingNumber trackingNumber, Instant shippedAt) {
        eventPublisher.publish(new ShipmentCreatedEvent(orderId, trackingNumber, shippedAt));
    }
}
