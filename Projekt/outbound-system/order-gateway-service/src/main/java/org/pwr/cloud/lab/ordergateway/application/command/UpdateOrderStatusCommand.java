package org.pwr.cloud.lab.ordergateway.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;

public record UpdateOrderStatusCommand(OrderId orderId, OrderStatus orderStatus, String reason)
        implements Command<Void> {}
