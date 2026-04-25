package org.pwr.cloud.lab.picking.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;

import java.util.Optional;

public interface PickingTaskRepository {
    PickingTask save(PickingTask task);

    Optional<PickingTask> findByOrderId(OrderId orderId);
}
