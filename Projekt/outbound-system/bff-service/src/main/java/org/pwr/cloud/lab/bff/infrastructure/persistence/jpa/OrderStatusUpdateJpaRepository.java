package org.pwr.cloud.lab.bff.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;
import org.pwr.cloud.lab.bff.infrastructure.persistence.entity.OrderStatusUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface OrderStatusUpdateJpaRepository extends JpaRepository<OrderStatusUpdateEntity, Long> {
    List<OrderStatusUpdateEntity> findAllByOrderIdOrderByTimestampAsc(String orderId);

    List<OrderStatusUpdateEntity> findByTimestampBetween(Instant from, Instant to);

    List<OrderStatusUpdateEntity> findByEventTypeAndTimestampBetween(SseEventType eventType, Instant from, Instant to);

    List<OrderStatusUpdateEntity> findByOrderIdIn(Collection<String> orderIds);
}
