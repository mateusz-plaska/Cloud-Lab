package org.pwr.cloud.lab.packing.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.event.OrderPickedEvent;
import org.pwr.cloud.lab.packing.application.command.CreatePackingTaskCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPickedEventListener {
    private final Mediator mediator;

    @RabbitListener(queues = "${rabbitmq.packing.queue.name}")
    public void handleOrderPickedEvent(OrderPickedEvent event) {
        mediator.send(new CreatePackingTaskCommand(event.orderId()));
    }
}
