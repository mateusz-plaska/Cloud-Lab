package org.pwr.cloudlab.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.model.event.ContainerPickedEvent;

@Slf4j
public class ContainerRoutingService {

    public void handleContainerPicked(ContainerPickedEvent event) {
        log.info("Received: {}", event);
    }
}
