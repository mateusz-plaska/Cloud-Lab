package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;

@Builder
public record AllocationFailedEvent(OrderId orderId, String reason) implements DomainEvent {}
