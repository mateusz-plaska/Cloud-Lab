package org.pwr.cloud.lab.common.domain.event;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record OrderPickFailedEvent(OrderId orderId, ProductId productId, String reason) implements DomainEvent {}
