package org.pwr.cloud.lab.picking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OrderPickFailedEvent;
import org.pwr.cloud.lab.common.domain.event.OrderPickedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.common.messagebroker.EventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PickingRabbitMqService {
    private final EventPublisher eventPublisher;

    public void sendPickingCompletedEvent(OrderId orderId) {
        eventPublisher.publish(new OrderPickedEvent(orderId));
    }

    public void sendPickingFailedEvent(OrderId orderId, ProductId productId, String reason) {
        eventPublisher.publish(new OrderPickFailedEvent(orderId, productId, reason));
    }
}
