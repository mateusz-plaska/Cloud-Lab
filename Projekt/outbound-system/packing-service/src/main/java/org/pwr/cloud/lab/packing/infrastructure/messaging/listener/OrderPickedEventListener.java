package org.pwr.cloud.lab.packing.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OrderPickedEvent;
import org.pwr.cloud.lab.packing.application.service.PackingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPickedEventListener {
    private final PackingService packingService;

    @RabbitListener(queues = "${rabbitmq.packing.queue.name}")
    public void handleOrderPickedEvent(OrderPickedEvent event) {
        packingService.createPackingTask(event.orderId());
    }
}
