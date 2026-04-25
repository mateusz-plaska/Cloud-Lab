package org.pwr.cloud.lab.packing.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.domain.model.PackingTask;
import org.pwr.cloud.lab.packing.domain.repository.PackingTaskRepository;
import org.pwr.cloud.lab.packing.infrastructure.persistence.entity.PackingTaskEntity;
import org.pwr.cloud.lab.packing.infrastructure.persistence.jpa.PackingTaskJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PackingTaskRepositoryImpl implements PackingTaskRepository {
    private final PackingTaskJpaRepository packingTaskJpaRepository;

    @Override
    public PackingTask save(PackingTask task) {
        packingTaskJpaRepository.save(toEntity(task));
        return task;
    }

    @Override
    public Optional<PackingTask> findByOrderId(OrderId orderId) {
        return packingTaskJpaRepository.findById(orderId.value()).map(this::toDomain);
    }

    private PackingTaskEntity toEntity(PackingTask task) {
        return PackingTaskEntity.builder()
                .orderId(task.orderId().value())
                .status(task.status())
                .boxSize(task.boxSize())
                .weight(task.weight())
                .build();
    }

    private PackingTask toDomain(PackingTaskEntity entity) {
        return PackingTask.builder()
                .orderId(OrderId.of(entity.getOrderId()))
                .status(entity.getStatus())
                .boxSize(entity.getBoxSize())
                .weight(entity.getWeight())
                .build();
    }
}
