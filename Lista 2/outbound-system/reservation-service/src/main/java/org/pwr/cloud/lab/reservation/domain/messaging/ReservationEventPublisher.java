package org.pwr.cloud.lab.reservation.domain.messaging;

import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

import java.util.List;

public interface ReservationEventPublisher {
    void publishStockReserved(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items);

    void publishAllocationFailed(OrderId orderId, String reason);
}
