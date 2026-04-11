package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.util.List;

@Builder
public record StockReservedEvent(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items)
        implements DomainEvent {}
