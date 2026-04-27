package org.pwr.cloud.lab.picking.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.picking.domain.model.PickingItem;
import org.pwr.cloud.lab.picking.domain.model.PickingStatus;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class PickingTaskPayload {
    private String orderId;
    private String status;
    private List<ItemPayload> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @DynamoDbBean
    public static class ItemPayload {
        private String productId;
        private int requiredQuantity;
        private int pickedQuantity;
    }

    public static PickingTaskPayload fromDomain(PickingTask task) {
        var items = task.items().stream()
                .map(i -> ItemPayload.builder()
                        .productId(i.productId().value())
                        .requiredQuantity(i.requiredQuantity())
                        .pickedQuantity(i.pickedQuantity())
                        .build())
                .collect(Collectors.toList());

        return PickingTaskPayload.builder()
                .orderId(task.orderId().value())
                .status(task.status().name())
                .items(items)
                .build();
    }

    public PickingTask toDomain() {
        var domainItems = items.stream()
                .map(i -> PickingItem.builder()
                        .productId(ProductId.of(i.getProductId()))
                        .requiredQuantity(i.getRequiredQuantity())
                        .pickedQuantity(i.getPickedQuantity())
                        .build())
                .collect(Collectors.toList());

        return PickingTask.builder()
                .orderId(OrderId.of(this.orderId))
                .status(PickingStatus.valueOf(this.status))
                .items(domainItems)
                .build();
    }
}
