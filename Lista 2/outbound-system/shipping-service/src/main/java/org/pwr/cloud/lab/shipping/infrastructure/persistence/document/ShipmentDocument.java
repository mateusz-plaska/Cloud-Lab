package org.pwr.cloud.lab.shipping.infrastructure.persistence.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "shipments")
@Getter
@Builder
public class ShipmentDocument {
    @Id
    private String orderId;

    private String trackingNumber;

    private BigDecimal shippingCost;

    private Instant shippedAt;
}
