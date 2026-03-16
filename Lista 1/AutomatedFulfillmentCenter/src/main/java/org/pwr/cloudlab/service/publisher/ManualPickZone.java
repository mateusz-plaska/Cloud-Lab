package org.pwr.cloudlab.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.broker.MessageBroker;
import org.pwr.cloudlab.model.event.ContainerPickedEvent;
import org.pwr.cloudlab.model.event.PickType;

@Slf4j
public record ManualPickZone(MessageBroker broker) implements Runnable {

    @Override
    public void run() {
        var containerPickedEvent = ContainerPickedEvent.from(PickType.MANUAL);
        log.info("Publishing {}", containerPickedEvent);
        broker.publish(containerPickedEvent);
    }
}
