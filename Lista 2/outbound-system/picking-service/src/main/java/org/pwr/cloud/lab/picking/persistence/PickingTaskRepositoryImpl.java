package org.pwr.cloud.lab.picking.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.PickingTaskId;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.picking.domain.PickingItem;
import org.pwr.cloud.lab.picking.domain.PickingStatus;
import org.pwr.cloud.lab.picking.domain.PickingTask;
import org.pwr.cloud.lab.picking.domain.PickingTaskRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PickingTaskRepositoryImpl implements PickingTaskRepository {
    private final PickingTaskMongoRepository pickingTaskMongoRepository;

    @Override
    public PickingTask save(PickingTask task) {
        var saved = pickingTaskMongoRepository.save(toDocument(task));
        return toDomain(saved);
    }

    @Override
    public Optional<PickingTask> findByOrderId(OrderId orderId) {
        return pickingTaskMongoRepository.findByOrderId(orderId.value()).map(this::toDomain);
    }

    private PickingTaskDocument toDocument(PickingTask task) {
        return PickingTaskDocument.builder()
                .id(task.pickingTaskId().value())
                .orderId(task.orderId().value())
                .status(task.status().name())
                .items(task.items().stream().map(this::toDocument).toList())
                .build();
    }

    private PickingTask toDomain(PickingTaskDocument doc) {
        return PickingTask.builder()
                .pickingTaskId(PickingTaskId.of(doc.getId()))
                .orderId(OrderId.of(doc.getOrderId()))
                .status(PickingStatus.valueOf(doc.getStatus()))
                .items(doc.getItems().stream().map(this::toDomain).toList())
                .build();
    }

    private PickingItemDocument toDocument(PickingItem item) {
        return PickingItemDocument.builder()
                .productId(item.productId().value())
                .requiredQuantity(item.requiredQuantity())
                .pickedQuantity(item.pickedQuantity())
                .build();
    }

    private PickingItem toDomain(PickingItemDocument doc) {
        return PickingItem.builder()
                .productId(ProductId.of(doc.getProductId()))
                .requiredQuantity(doc.getRequiredQuantity())
                .pickedQuantity(doc.getPickedQuantity())
                .build();
    }
}
