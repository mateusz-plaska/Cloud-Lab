package org.pwr.cloud.lab.shipping.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.shipping.application.ShippingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackingFinishedEventListener {
    private final ShippingService shippingService;

    @RabbitListener(queues = "${rabbitmq.shipping.queue.name}")
    public void handlePackingFinishedEvent(PackingFinishedEvent event) {
        shippingService.createShipment(event);
    }
}
