package org.pwr.cloud.lab.packing.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.packing.domain.BoxSize;
import org.pwr.cloud.lab.packing.domain.PackingStatus;
import org.pwr.cloud.lab.packing.domain.PackingTask;
import org.pwr.cloud.lab.packing.domain.PackingTaskRepository;
import org.pwr.cloud.lab.packing.infrastructure.PackingRabbitMqService;
import org.pwr.cloud.lab.packing.persistence.BoxTypeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PackingService {
    private final PackingTaskRepository packingTaskRepository;
    private final BoxTypeJpaRepository boxTypeRepository;
    private final PackingRabbitMqService rabbitMqService;

    public void createPackingTask(OrderId orderId) {
        var task = PackingTask.builder()
                .orderId(orderId)
                .status(PackingStatus.IN_PROGRESS)
                .build();
        packingTaskRepository.save(task);
    }

    public void finishPacking(OrderId orderId, BoxSize boxSize, double weight) {
        var task = packingTaskRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Packing task not found"));

        var boxType = boxTypeRepository.findById(boxSize).orElseThrow(() -> new RuntimeException("Box size not found"));

        var completedTask = task.toBuilder()
                .status(PackingStatus.COMPLETED)
                .boxSize(boxSize)
                .weight(weight)
                .build();

        packingTaskRepository.save(completedTask);
        rabbitMqService.sendPackingFinishedEvent(
                orderId, weight, boxSize, boxType.getLength(), boxType.getWidth(), boxType.getHeight());
    }
}
