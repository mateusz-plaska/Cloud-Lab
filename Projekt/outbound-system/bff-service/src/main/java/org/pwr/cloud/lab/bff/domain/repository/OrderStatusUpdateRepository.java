package org.pwr.cloud.lab.bff.domain.repository;

import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;

import java.util.List;

public interface OrderStatusUpdateRepository {
    void save(OrderStatusUpdate update);
    List<OrderStatusUpdate> findAllByOrderIdOrderedByTimestampAsc(String orderId);
}
