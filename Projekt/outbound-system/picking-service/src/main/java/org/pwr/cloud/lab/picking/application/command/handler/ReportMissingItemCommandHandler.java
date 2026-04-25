package org.pwr.cloud.lab.picking.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.picking.application.command.ReportMissingItemCommand;
import org.pwr.cloud.lab.picking.domain.exception.PickingTaskNotFoundException;
import org.pwr.cloud.lab.picking.domain.messaging.PickingEventPublisher;
import org.pwr.cloud.lab.picking.domain.repository.PickingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportMissingItemCommandHandler implements CommandHandler<ReportMissingItemCommand, Void> {
    private final PickingTaskRepository pickingTaskRepository;
    private final PickingEventPublisher pickingEventPublisher;

    @Override
    @Transactional
    public Void handle(ReportMissingItemCommand command) {
        var task = pickingTaskRepository
                .findByOrderId(command.orderId())
                .orElseThrow(() -> new PickingTaskNotFoundException(command.orderId()));

        var updatedTask = task.markAsFailed();
        pickingTaskRepository.save(updatedTask);
        pickingEventPublisher.publishPickingFailed(command.orderId(), command.productId(), command.reason());
        return null;
    }
}
