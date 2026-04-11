package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.ProductId;

import java.util.List;
import java.util.Map;

@Builder
public record OutboundOrderCreatedEvent(OrderId orderId, List<OrderItem> items, Map<String, String> metadata)
        implements DomainEvent {
    public record OrderItem(ProductId productId, Integer quantity) {}
}
