package org.pwr.cloudlab.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.broker.MessageBroker;
import org.pwr.cloudlab.config.Util;
import org.pwr.cloudlab.model.event.OrderCreatedEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public record StoreService(MessageBroker broker,
                           ScheduledExecutorService scheduledExecutorService) implements Runnable {
    @Override
    public void run() {
        var orderCreatedEvent = OrderCreatedEvent.from();
        log.info("Publishing {}", orderCreatedEvent);
        broker.publish(orderCreatedEvent);

        var delay = Util.someInteger(9, 25);
        scheduledExecutorService.schedule(this, delay, TimeUnit.SECONDS);
    }
}
