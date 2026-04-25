package org.pwr.cloud.lab.shipping.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.shipping.application.command.CreateShipmentCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackingFinishedEventListener {
    private final Mediator mediator;

    @RabbitListener(queues = "${rabbitmq.shipping.queue.name}")
    public void handlePackingFinishedEvent(PackingFinishedEvent event) {
        mediator.send(new CreateShipmentCommand(event.orderId(), event.weight(), event.boxType()));
    }
}
