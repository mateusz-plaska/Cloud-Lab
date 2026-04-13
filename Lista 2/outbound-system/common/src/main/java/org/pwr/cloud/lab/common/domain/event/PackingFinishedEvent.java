package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

@Builder(toBuilder = true)
public record PackingFinishedEvent(OrderId orderId, double weight, BoxType boxType) implements DomainEvent {}
