package org.pwr.cloud.lab.reservation.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

import java.util.List;

public record ReserveItemsCommand(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items)
        implements Command<Void> {}
