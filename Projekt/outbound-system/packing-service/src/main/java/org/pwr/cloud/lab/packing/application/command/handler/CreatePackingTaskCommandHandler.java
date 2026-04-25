package org.pwr.cloud.lab.packing.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.packing.application.command.CreatePackingTaskCommand;
import org.pwr.cloud.lab.packing.domain.model.PackingStatus;
import org.pwr.cloud.lab.packing.domain.model.PackingTask;
import org.pwr.cloud.lab.packing.domain.repository.PackingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePackingTaskCommandHandler implements CommandHandler<CreatePackingTaskCommand, Void> {
    private final PackingTaskRepository packingTaskRepository;

    @Override
    @Transactional
    public Void handle(CreatePackingTaskCommand command) {
        var task = PackingTask.builder()
                .orderId(command.orderId())
                .status(PackingStatus.IN_PROGRESS)
                .build();
        packingTaskRepository.save(task);
        return null;
    }
}
