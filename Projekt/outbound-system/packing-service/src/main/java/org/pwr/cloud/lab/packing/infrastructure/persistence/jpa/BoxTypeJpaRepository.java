package org.pwr.cloud.lab.packing.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.packing.infrastructure.persistence.entity.BoxTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxTypeJpaRepository extends JpaRepository<BoxTypeEntity, BoxSize> {}
