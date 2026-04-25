package org.pwr.cloud.lab.picking.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.picking.application.command.PickItemCommand;
import org.pwr.cloud.lab.picking.domain.exception.PickingTaskNotFoundException;
import org.pwr.cloud.lab.picking.domain.messaging.PickingEventPublisher;
import org.pwr.cloud.lab.picking.domain.model.PickingStatus;
import org.pwr.cloud.lab.picking.domain.repository.PickingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PickItemCommandHandler implements CommandHandler<PickItemCommand, Void> {
    private final PickingTaskRepository pickingTaskRepository;
    private final PickingEventPublisher pickingEventPublisher;

    @Override
    @Transactional
    public Void handle(PickItemCommand command) {
        var task = pickingTaskRepository
                .findByOrderId(command.orderId())
                .orElseThrow(() -> new PickingTaskNotFoundException(command.orderId()));

        var updatedTask = task.pickItem(command.productId(), command.quantity());
        pickingTaskRepository.save(updatedTask);
        if (updatedTask.status() == PickingStatus.COMPLETED) {
            pickingEventPublisher.publishPickingCompleted(command.orderId());
        }
        return null;
    }
}
