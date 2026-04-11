package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;

@Builder(toBuilder = true)
public record PackingFinishedEvent(
        OrderId orderId, double weight, String boxSize, double length, double width, double height)
        implements DomainEvent {}
