package org.pwr.cloud.lab.shipping.infrastructure.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.shipping.application.command.CreateShipmentCommand;
import org.pwr.cloud.lab.shipping.domain.repository.PendingDispatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingDispatchScheduler {

    private final PendingDispatchRepository pendingDispatchRepository;
    private final Mediator mediator;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void processDueDispatches() {
        var due = pendingDispatchRepository.findAllDue();
        if (due.isEmpty()) return;

        log.info("Processing {} due shipment dispatch(es)", due.size());
        due.forEach(dispatch -> {
            try {
                mediator.send(new CreateShipmentCommand(dispatch.orderId(), dispatch.weight(), dispatch.boxType()));
                pendingDispatchRepository.delete(dispatch.orderId());
                log.info("Shipment created for order [{}]", dispatch.orderId());
            } catch (Exception e) {
                log.error("Failed to create shipment for order [{}]: {}", dispatch.orderId(), e.getMessage());
            }
        });
    }
}
