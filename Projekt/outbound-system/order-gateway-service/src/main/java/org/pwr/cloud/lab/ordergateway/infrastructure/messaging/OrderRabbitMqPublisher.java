package org.pwr.cloud.lab.ordergateway.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.messaging.EventPublisher;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.messaging.OrderEventPublisher;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderRabbitMqPublisher implements OrderEventPublisher {
    private final EventPublisher eventPublisher;

    @Override
    public void publishOrderCreated(OrderId orderId, List<OrderItem> items, Map<String, String> metadata) {
        eventPublisher.publish(OutboundOrderCreatedEvent.builder()
                .orderId(orderId)
                .items(toEventItems(items))
                .metadata(metadata)
                .build());
    }

    private List<OutboundOrderCreatedEvent.OrderItem> toEventItems(List<OrderItem> items) {
        return items.stream()
                .map(i -> new OutboundOrderCreatedEvent.OrderItem(i.productId(), i.quantity()))
                .toList();
    }
}
