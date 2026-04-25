package org.pwr.cloud.lab.packing.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.packing.application.command.FinishPackingCommand;
import org.pwr.cloud.lab.packing.domain.exception.BoxSizeNotFoundException;
import org.pwr.cloud.lab.packing.domain.exception.PackingTaskNotFoundException;
import org.pwr.cloud.lab.packing.domain.messaging.PackingEventPublisher;
import org.pwr.cloud.lab.packing.domain.repository.BoxTypeRepository;
import org.pwr.cloud.lab.packing.domain.repository.PackingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinishPackingCommandHandler implements CommandHandler<FinishPackingCommand, Void> {
    private final PackingTaskRepository packingTaskRepository;
    private final BoxTypeRepository boxTypeRepository;
    private final PackingEventPublisher packingEventPublisher;

    @Override
    @Transactional
    public Void handle(FinishPackingCommand command) {
        var task = packingTaskRepository
                .findByOrderId(command.orderId())
                .orElseThrow(() -> new PackingTaskNotFoundException(command.orderId()));

        var boxType = boxTypeRepository
                .findBySize(command.boxSize())
                .orElseThrow(() -> new BoxSizeNotFoundException(
                        command.orderId(), command.boxSize().name()));

        var completedTask = task.finishPacking(command.boxSize(), command.weight());
        packingTaskRepository.save(completedTask);
        packingEventPublisher.publishPackingFinished(command.orderId(), command.weight(), boxType);
        return null;
    }
}
