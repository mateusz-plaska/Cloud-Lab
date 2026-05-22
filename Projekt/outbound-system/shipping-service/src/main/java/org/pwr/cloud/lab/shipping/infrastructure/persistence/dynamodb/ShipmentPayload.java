package org.pwr.cloud.lab.shipping.infrastructure.persistence.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ShipmentPayload {
    private String orderId;
    private String trackingNumber;
    private BigDecimal shippingCost;
    private Long shippedAt;

    public static ShipmentPayload fromDomain(Shipment shipment) {
        return ShipmentPayload.builder()
                .orderId(shipment.orderId().value())
                .trackingNumber(shipment.trackingNumber().value())
                .shippingCost(shipment.shippingCost())
                .shippedAt(shipment.shippedAt().toEpochMilli())
                .build();
    }

    public Shipment toDomain() {
        return Shipment.builder()
                .orderId(OrderId.of(this.orderId))
                .trackingNumber(TrackingNumber.of(this.trackingNumber))
                .shippingCost(this.shippingCost)
                .shippedAt(Instant.ofEpochMilli(this.shippedAt))
                .build();
    }
}
