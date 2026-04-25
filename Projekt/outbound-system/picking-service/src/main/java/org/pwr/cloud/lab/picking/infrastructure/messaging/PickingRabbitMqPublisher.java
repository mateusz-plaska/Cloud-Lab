package org.pwr.cloud.lab.picking.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OrderPickFailedEvent;
import org.pwr.cloud.lab.common.domain.event.OrderPickedEvent;
import org.pwr.cloud.lab.common.domain.messaging.EventPublisher;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.picking.domain.messaging.PickingEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PickingRabbitMqPublisher implements PickingEventPublisher {
    private final EventPublisher eventPublisher;

    @Override
    public void publishPickingCompleted(OrderId orderId) {
        eventPublisher.publish(new OrderPickedEvent(orderId));
    }

    @Override
    public void publishPickingFailed(OrderId orderId, ProductId productId, String reason) {
        eventPublisher.publish(new OrderPickFailedEvent(orderId, productId, reason));
    }
}
