package org.pwr.cloud.lab.shipping.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record Shipment(OrderId orderId, TrackingNumber trackingNumber, BigDecimal shippingCost, Instant shippedAt) {}
