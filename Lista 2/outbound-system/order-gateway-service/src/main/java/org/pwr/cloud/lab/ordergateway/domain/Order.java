package org.pwr.cloud.lab.ordergateway.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.CustomerId;
import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record Order(
        OrderId orderId,
        CustomerId customerId,
        OrderStatus status,
        List<OrderItem> items,
        Map<String, String> metadata,
        Instant createdAt,
        Instant updatedAt) {}
