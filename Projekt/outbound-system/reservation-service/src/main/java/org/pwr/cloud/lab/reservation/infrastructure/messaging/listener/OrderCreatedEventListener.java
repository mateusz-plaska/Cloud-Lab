package org.pwr.cloud.lab.reservation.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.reservation.application.command.ReserveItemsCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {
    private final Mediator mediator;

    @RabbitListener(queues = "${rabbitmq.reservation.queue.name}")
    public void handleOrderCreated(OutboundOrderCreatedEvent event) {
        mediator.send(new ReserveItemsCommand(event.orderId(), event.items()));
    }
}
