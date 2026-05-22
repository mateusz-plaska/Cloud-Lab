package org.pwr.cloud.lab.ordergateway.api.dto;

import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;

import java.time.Instant;

public record OrderListItemDto(
        OrderId orderId,
        CustomerId customerId,
        OrderStatus status,
        int itemCount,
        Instant createdAt,
        Instant updatedAt) {}
