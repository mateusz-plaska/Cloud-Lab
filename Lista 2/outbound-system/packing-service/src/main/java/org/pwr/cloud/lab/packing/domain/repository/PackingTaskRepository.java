package org.pwr.cloud.lab.packing.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.domain.model.PackingTask;

import java.util.Optional;

public interface PackingTaskRepository {
    PackingTask save(PackingTask task);

    Optional<PackingTask> findByOrderId(OrderId orderId);
}
