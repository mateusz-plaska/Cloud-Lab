package org.pwr.cloud.lab.reservation.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.reservation.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {}
