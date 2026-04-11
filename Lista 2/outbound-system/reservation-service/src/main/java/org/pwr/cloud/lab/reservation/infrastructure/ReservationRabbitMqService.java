package org.pwr.cloud.lab.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.AllocationFailedEvent;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.messagebroker.EventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationRabbitMqService {
    private final EventPublisher eventPublisher;

    public void sendStockReserved(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items) {
        eventPublisher.publish(new StockReservedEvent(orderId, items));
    }

    public void sendAllocationFailed(OrderId orderId, String reason) {
        eventPublisher.publish(new AllocationFailedEvent(orderId, reason));
    }
}
