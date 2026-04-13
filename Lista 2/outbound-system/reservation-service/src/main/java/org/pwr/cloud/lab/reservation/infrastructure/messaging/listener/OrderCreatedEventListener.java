package org.pwr.cloud.lab.reservation.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.reservation.application.service.ReservationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {
    private final ReservationService reservationService;

    @RabbitListener(queues = "${rabbitmq.reservation.queue.name}")
    public void handleOrderCreated(OutboundOrderCreatedEvent event) {
        reservationService.reserve(event.orderId(), event.items());
    }
}
