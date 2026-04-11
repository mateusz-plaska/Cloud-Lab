package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;

@Builder
public record StockReservedEvent(OrderId orderId) implements DomainEvent {}
