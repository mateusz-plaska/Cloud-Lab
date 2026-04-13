package org.pwr.cloud.lab.picking.application.service;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.PickingTaskId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.picking.domain.exception.PickingTaskNotFoundException;
import org.pwr.cloud.lab.picking.domain.messaging.PickingEventPublisher;
import org.pwr.cloud.lab.picking.domain.model.PickingItem;
import org.pwr.cloud.lab.picking.domain.model.PickingStatus;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;
import org.pwr.cloud.lab.picking.domain.repository.PickingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PickingService {
    private final PickingTaskRepository pickingTaskRepository;
    private final PickingEventPublisher pickingEventPublisher;

    public void createPickingTask(OrderId orderId, List<PickingItem> items) {
        var pickingTask = PickingTask.builder()
                .pickingTaskId(PickingTaskId.newInstance())
                .orderId(orderId)
                .status(PickingStatus.PLANNED)
                .items(items)
                .build();

        pickingTaskRepository.save(pickingTask);
    }

    public void pickItem(OrderId orderId, ProductId productId, int quantity) {
        var task = pickingTaskRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new PickingTaskNotFoundException(orderId));

        var updatedTask = task.pickItem(productId, quantity);
        pickingTaskRepository.save(updatedTask);
        if (updatedTask.status() == PickingStatus.COMPLETED) {
            pickingEventPublisher.publishPickingCompleted(orderId);
        }
    }

    public void reportMissingItem(OrderId orderId, ProductId productId, String reason) {
        var task = pickingTaskRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new PickingTaskNotFoundException(orderId));

        var updatedTask = task.markAsFailed();
        pickingTaskRepository.save(updatedTask);
        pickingEventPublisher.publishPickingFailed(orderId, productId, reason);
    }
}
