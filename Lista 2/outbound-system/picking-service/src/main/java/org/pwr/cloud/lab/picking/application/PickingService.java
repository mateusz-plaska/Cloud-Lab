package org.pwr.cloud.lab.picking.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.PickingTaskId;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.picking.domain.PickingItem;
import org.pwr.cloud.lab.picking.domain.PickingStatus;
import org.pwr.cloud.lab.picking.domain.PickingTask;
import org.pwr.cloud.lab.picking.domain.PickingTaskRepository;
import org.pwr.cloud.lab.picking.infrastructure.PickingRabbitMqService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PickingService {
    private final PickingTaskRepository pickingTaskRepository;
    private final PickingRabbitMqService pickingRabbitMqService;

    public void createPickingTask(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items) {
        var pickingTask = PickingTask.builder()
                .pickingTaskId(PickingTaskId.newInstance())
                .orderId(orderId)
                .status(PickingStatus.PLANNED)
                .items(convertToPickingItems(items))
                .build();

        pickingTaskRepository.save(pickingTask);
    }

    public void pickItem(OrderId orderId, ProductId productId, int quantity) {
        var task = pickingTaskRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Picking task not found"));

        var updatedItems = task.items().stream()
                .map(item -> item.productId().equals(productId)
                        ? item.toBuilder()
                                .pickedQuantity(item.pickedQuantity() + quantity)
                                .build()
                        : item)
                .toList();

        var updatedTask = task.toBuilder()
                .items(updatedItems)
                .status(PickingStatus.IN_PROGRESS)
                .build();

        if (updatedTask.isCompleted()) {
            updatedTask =
                    updatedTask.toBuilder().status(PickingStatus.COMPLETED).build();
            pickingRabbitMqService.sendPickingCompletedEvent(orderId);
        }

        pickingTaskRepository.save(updatedTask);
    }

    public void reportMissingItem(OrderId orderId, ProductId productId, String reason) {
        var task = pickingTaskRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Picking task not found"));

        var updatedTask = task.toBuilder().status(PickingStatus.FAILED).build();

        pickingTaskRepository.save(updatedTask);
        pickingRabbitMqService.sendPickingFailedEvent(orderId, productId, reason);
    }

    private List<PickingItem> convertToPickingItems(List<OutboundOrderCreatedEvent.OrderItem> items) {
        return items.stream()
                .map(item -> PickingItem.builder()
                        .productId(item.productId())
                        .requiredQuantity(item.quantity())
                        .pickedQuantity(0)
                        .build())
                .toList();
    }
}
