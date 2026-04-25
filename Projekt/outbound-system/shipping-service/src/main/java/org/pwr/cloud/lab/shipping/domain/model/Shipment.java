package org.pwr.cloud.lab.shipping.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record Shipment(OrderId orderId, TrackingNumber trackingNumber, BigDecimal shippingCost, Instant shippedAt) {

    public static BigDecimal calculateShippingCost(double weight, BoxType boxType) {
        double cost = 10.0 + (weight * 2.0) + (boxType.volume() / 1000.0);
        return BigDecimal.valueOf(cost).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
