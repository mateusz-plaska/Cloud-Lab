package org.pwr.cloud.lab.picking.domain;

import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.util.Optional;

public interface PickingTaskRepository {
    PickingTask save(PickingTask task);

    Optional<PickingTask> findByOrderId(OrderId orderId);
}
