package org.pwr.cloud.lab.bff.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.bff.infrastructure.persistence.entity.OrderStatusUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusUpdateJpaRepository extends JpaRepository<OrderStatusUpdateEntity, Long> {
    List<OrderStatusUpdateEntity> findAllByOrderIdOrderByTimestampAsc(String orderId);
}
