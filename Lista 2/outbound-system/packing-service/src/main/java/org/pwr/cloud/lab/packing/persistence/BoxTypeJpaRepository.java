package org.pwr.cloud.lab.packing.persistence;

import org.pwr.cloud.lab.packing.domain.BoxSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxTypeJpaRepository extends JpaRepository<BoxTypeEntity, BoxSize> {}
