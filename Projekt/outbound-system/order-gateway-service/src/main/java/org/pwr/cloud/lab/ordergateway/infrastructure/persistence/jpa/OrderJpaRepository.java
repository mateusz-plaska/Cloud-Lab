package org.pwr.cloud.lab.ordergateway.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.ordergateway.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
}
