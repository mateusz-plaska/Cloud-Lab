package org.pwr.cloud.lab.picking.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.PickingTaskId;

import java.util.List;

@Builder(toBuilder = true)
public record PickingTask(PickingTaskId pickingTaskId, OrderId orderId, PickingStatus status, List<PickingItem> items) {

    public boolean isCompleted() {
        return items.stream().allMatch(PickingItem::isPicked);
    }
}
