package org.pwr.cloud.lab.common.domain.event;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;

import java.time.Instant;

@Builder(toBuilder = true)
public record ShipmentCreatedEvent(OrderId orderId, TrackingNumber trackingNumber, Instant shippedAt)
        implements DomainEvent {}
