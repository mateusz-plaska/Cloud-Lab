package org.pwr.cloud.lab.picking.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class PickingTaskTableRow extends DynamoBaseTable<PickingTask> {

    private PickingTaskPayload payload;

    @Override
    public PickingTask toModel() {
        if (this.payload == null) {
            throw new IllegalStateException("Payload is missing in DynamoDB record");
        }
        return this.payload.toDomain();
    }

    public static PickingTaskTableRow from(PickingTask task) {
        return PickingTaskTableRow.builder()
                .pk(Indexes.PrimaryIndex.pk(task.orderId()))
                .payload(PickingTaskPayload.fromDomain(task))
                .lastModifiedTimestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static final class Indexes {
        private static final String ENTITY_NAME = "pickingTask";
        private static final String ORDER_ID_NAME = "orderId";

        public static final class PrimaryIndex {
            public static String pk(OrderId orderId) {
                return String.join("#", ENTITY_NAME, ORDER_ID_NAME, orderId.value());
            }
        }
    }
}
