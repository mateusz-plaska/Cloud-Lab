package org.pwr.cloud.lab.packing.application.service;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.domain.exception.BoxSizeNotFoundException;
import org.pwr.cloud.lab.packing.domain.exception.PackingTaskNotFoundException;
import org.pwr.cloud.lab.packing.domain.messaging.PackingEventPublisher;
import org.pwr.cloud.lab.packing.domain.model.PackingStatus;
import org.pwr.cloud.lab.packing.domain.model.PackingTask;
import org.pwr.cloud.lab.packing.domain.repository.BoxTypeRepository;
import org.pwr.cloud.lab.packing.domain.repository.PackingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PackingService {
    private final PackingTaskRepository packingTaskRepository;
    private final BoxTypeRepository boxTypeRepository;
    private final PackingEventPublisher packingEventPublisher;

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
                .orElseThrow(() -> new PackingTaskNotFoundException(orderId));

        var boxType = boxTypeRepository
                .findBySize(boxSize)
                .orElseThrow(() -> new BoxSizeNotFoundException(orderId, boxSize.name()));

        var completedTask = task.finishPacking(boxSize, weight);
        packingTaskRepository.save(completedTask);
        packingEventPublisher.publishPackingFinished(orderId, weight, boxType);
    }
}
