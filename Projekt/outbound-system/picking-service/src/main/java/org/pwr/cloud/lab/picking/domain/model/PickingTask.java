package org.pwr.cloud.lab.picking.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

import java.util.List;

@Builder(toBuilder = true)
public record PickingTask(OrderId orderId, PickingStatus status, List<PickingItem> items) {

    public boolean isCompleted() {
        return items.stream().allMatch(PickingItem::isPicked);
    }

    public PickingTask pickItem(ProductId productId, int quantity) {
        var updatedItems = this.items().stream()
                .map(item -> item.productId().equals(productId)
                        ? item.toBuilder()
                                .pickedQuantity(item.pickedQuantity() + quantity)
                                .build()
                        : item)
                .toList();

        var updatedTask = this.toBuilder()
                .items(updatedItems)
                .status(PickingStatus.IN_PROGRESS)
                .build();

        if (updatedTask.isCompleted()) {
            return updatedTask.toBuilder().status(PickingStatus.COMPLETED).build();
        }
        return updatedTask;
    }

    public PickingTask markAsFailed() {
        return this.toBuilder().status(PickingStatus.FAILED).build();
    }
}
