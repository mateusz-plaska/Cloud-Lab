package org.pwr.cloud.lab.packing.domain;

import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.util.Optional;

public interface PackingTaskRepository {
    PackingTask save(PackingTask task);

    Optional<PackingTask> findByOrderId(OrderId orderId);
}
