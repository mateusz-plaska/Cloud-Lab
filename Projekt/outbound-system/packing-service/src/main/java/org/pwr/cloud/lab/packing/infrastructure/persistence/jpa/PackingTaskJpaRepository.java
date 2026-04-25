package org.pwr.cloud.lab.packing.infrastructure.persistence.jpa;

import org.pwr.cloud.lab.packing.infrastructure.persistence.entity.PackingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackingTaskJpaRepository extends JpaRepository<PackingTaskEntity, String> {}
