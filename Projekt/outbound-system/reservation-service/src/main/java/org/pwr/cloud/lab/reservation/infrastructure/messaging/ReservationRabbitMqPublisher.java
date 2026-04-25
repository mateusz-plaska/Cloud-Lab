package org.pwr.cloud.lab.reservation.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.AllocationFailedEvent;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.pwr.cloud.lab.common.domain.messaging.EventPublisher;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.reservation.domain.messaging.ReservationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationRabbitMqPublisher implements ReservationEventPublisher {
    private final EventPublisher eventPublisher;

    @Override
    public void publishStockReserved(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items) {
        eventPublisher.publish(new StockReservedEvent(orderId, items));
    }

    @Override
    public void publishAllocationFailed(OrderId orderId, String reason) {
        eventPublisher.publish(new AllocationFailedEvent(orderId, reason));
    }
}
