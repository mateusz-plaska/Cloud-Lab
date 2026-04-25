package org.pwr.cloud.lab.picking.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record PickItemCommand(OrderId orderId, ProductId productId, int quantity) implements Command<Void> {}
