package org.pwr.cloud.lab.picking.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.picking.domain.model.PickingItem;

import java.util.List;

public record CreatePickingTaskCommand(OrderId orderId, List<PickingItem> items) implements Command<Void> {}
