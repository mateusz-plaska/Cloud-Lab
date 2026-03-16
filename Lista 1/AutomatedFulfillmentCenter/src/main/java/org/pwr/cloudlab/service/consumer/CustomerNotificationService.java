package org.pwr.cloudlab.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.model.event.ShipmentCreatedEvent;

@Slf4j
public class CustomerNotificationService {

    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        log.info("Received: {}", event);
    }
}
