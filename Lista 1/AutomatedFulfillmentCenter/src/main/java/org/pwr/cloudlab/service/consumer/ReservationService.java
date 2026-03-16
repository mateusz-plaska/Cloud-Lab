package org.pwr.cloudlab.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.model.event.OrderCreatedEvent;

@Slf4j
public class ReservationService {

    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received: {}", event);
    }
}
