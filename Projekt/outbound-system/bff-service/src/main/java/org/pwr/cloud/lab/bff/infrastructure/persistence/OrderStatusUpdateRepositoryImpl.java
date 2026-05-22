package org.pwr.cloud.lab.bff.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.domain.repository.OrderStatusUpdateRepository;
import org.pwr.cloud.lab.bff.infrastructure.persistence.entity.OrderStatusUpdateEntity;
import org.pwr.cloud.lab.bff.infrastructure.persistence.jpa.OrderStatusUpdateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderStatusUpdateRepositoryImpl implements OrderStatusUpdateRepository {

    private final OrderStatusUpdateJpaRepository orderStatusUpdateJpaRepository;

    @Override
    public void save(OrderStatusUpdate update) {
        orderStatusUpdateJpaRepository.save(toEntity(update));
    }

    @Override
    public List<OrderStatusUpdate> findAllByOrderIdOrderedByTimestampAsc(String orderId) {
        return orderStatusUpdateJpaRepository.findAllByOrderIdOrderByTimestampAsc(orderId).stream()
                .map(this::toDomain)
                .toList();
    }

    private OrderStatusUpdateEntity toEntity(OrderStatusUpdate update) {
        return OrderStatusUpdateEntity.builder()
                .orderId(update.orderId())
                .eventType(update.eventType())
                .station(update.station())
                .timestamp(update.timestamp())
                .build();
    }

    private OrderStatusUpdate toDomain(OrderStatusUpdateEntity entity) {
        return new OrderStatusUpdate(
                entity.getOrderId(), entity.getEventType(), entity.getStation(), entity.getTimestamp());
    }
}
