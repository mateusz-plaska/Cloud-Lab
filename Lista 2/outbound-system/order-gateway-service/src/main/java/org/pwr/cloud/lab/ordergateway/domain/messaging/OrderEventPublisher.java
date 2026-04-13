package org.pwr.cloud.lab.ordergateway.domain.messaging;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderId orderId, List<OrderItem> items, Map<String, String> metadata);
}
