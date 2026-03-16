package org.pwr.cloudlab.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.broker.MessageBroker;
import org.pwr.cloudlab.config.Util;
import org.pwr.cloudlab.model.event.OrderPackedEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public record PackingStation(MessageBroker broker,
                             ScheduledExecutorService scheduledExecutorService) implements Runnable {
    @Override
    public void run() {
        var orderPackedEvent = OrderPackedEvent.from();
        log.info("Publishing {}", orderPackedEvent);
        broker.publish(orderPackedEvent);

        var delay = Util.someInteger(9, 25);
        scheduledExecutorService.schedule(this, delay, TimeUnit.SECONDS);
    }
}
