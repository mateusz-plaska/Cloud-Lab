package org.pwr.cloud.lab.common.domain.event;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public record OrderPickedEvent(OrderId orderId) implements DomainEvent {}
