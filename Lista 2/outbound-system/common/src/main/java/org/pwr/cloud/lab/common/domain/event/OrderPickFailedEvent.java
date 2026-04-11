package org.pwr.cloud.lab.common.domain.event;

import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.ProductId;

public record OrderPickFailedEvent(OrderId orderId, ProductId productId, String reason) implements DomainEvent {}
