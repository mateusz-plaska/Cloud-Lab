package org.pwr.cloud.lab.shipping.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.shipping.domain.model.PendingDispatch;
import org.pwr.cloud.lab.shipping.domain.repository.PendingDispatchRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class PackingFinishedEventListener {

    private final PendingDispatchRepository pendingDispatchRepository;

    @Value("${shipping.dispatch-delay.min-seconds:30}")
    private int minDelaySeconds;

    @Value("${shipping.dispatch-delay.max-seconds:300}")
    private int maxDelaySeconds;

    @RabbitListener(queues = "${rabbitmq.shipping.queue.name}")
    public void handlePackingFinishedEvent(PackingFinishedEvent event) {
        int delaySeconds = minDelaySeconds + ThreadLocalRandom.current().nextInt(maxDelaySeconds - minDelaySeconds + 1);
        var dispatchAt = Instant.now().plusSeconds(delaySeconds);

        log.info("Scheduling shipment for order [{}] at {} (delay {}s)", event.orderId(), dispatchAt, delaySeconds);

        pendingDispatchRepository.save(
                new PendingDispatch(event.orderId(), event.weight(), event.boxType(), dispatchAt));
    }
}
