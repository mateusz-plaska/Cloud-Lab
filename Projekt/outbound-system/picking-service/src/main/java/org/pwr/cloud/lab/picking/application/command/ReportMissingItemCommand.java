package org.pwr.cloud.lab.picking.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record ReportMissingItemCommand(OrderId orderId, ProductId productId, String reason) implements Command<Void> {}
