package org.pwr.cloud.lab.reservation.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.reservation.infrastructure.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockJpaRepository extends JpaRepository<StockEntity, String> {}
