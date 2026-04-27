package org.pwr.cloud.lab.shipping.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ShipmentTableRow extends DynamoBaseTable<Shipment> {

    private ShipmentPayload payload;

    @Override
    public Shipment toModel() {
        return this.payload.toDomain();
    }

    public static ShipmentTableRow from(Shipment shipment) {
        return ShipmentTableRow.builder()
                .pk(Indexes.PrimaryIndex.pk(shipment.orderId()))
                .payload(ShipmentPayload.fromDomain(shipment))
                .lastModifiedTimestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static final class Indexes {
        private static final String ENTITY_NAME = "shipment";
        private static final String ORDER_ID_NAME = "orderId";

        public static final class PrimaryIndex {
            public static String pk(OrderId orderId) {
                return String.join("#", ENTITY_NAME, ORDER_ID_NAME, orderId.value());
            }
        }
    }
}
