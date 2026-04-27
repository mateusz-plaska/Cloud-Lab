package org.pwr.cloud.lab.picking.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.picking.application.command.CreatePickingTaskCommand;
import org.pwr.cloud.lab.picking.domain.model.PickingStatus;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;
import org.pwr.cloud.lab.picking.domain.repository.PickingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePickingTaskCommandHandler implements CommandHandler<CreatePickingTaskCommand, Void> {
    private final PickingTaskRepository pickingTaskRepository;

    @Override
    @Transactional
    public Void handle(CreatePickingTaskCommand command) {
        var pickingTask = PickingTask.builder()
                .orderId(command.orderId())
                .status(PickingStatus.PLANNED)
                .items(command.items())
                .build();

        pickingTaskRepository.save(pickingTask);
        return null;
    }
}
