package org.pwr.cloud.lab.shipping.infrastructure.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PendingDispatchJpaRepository extends JpaRepository<PendingDispatchEntity, Long> {

    @Query(
            value = "SELECT * FROM pending_dispatches WHERE dispatch_at <= NOW() FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    List<PendingDispatchEntity> findAllDue();

    void deleteByOrderId(String orderId);
}
