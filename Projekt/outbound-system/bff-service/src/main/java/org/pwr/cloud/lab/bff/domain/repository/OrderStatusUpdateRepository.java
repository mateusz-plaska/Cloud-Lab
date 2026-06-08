package org.pwr.cloud.lab.bff.domain.repository;

import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface OrderStatusUpdateRepository {
    void save(OrderStatusUpdate update);

    List<OrderStatusUpdate> findAllByOrderIdOrderedByTimestampAsc(String orderId);

    List<OrderStatusUpdate> findByTimestampBetween(Instant from, Instant to);

    List<OrderStatusUpdate> findByEventTypeAndTimestampBetween(SseEventType eventType, Instant from, Instant to);

    List<OrderStatusUpdate> findByOrderIdIn(Collection<String> orderIds);

    long count();
}
