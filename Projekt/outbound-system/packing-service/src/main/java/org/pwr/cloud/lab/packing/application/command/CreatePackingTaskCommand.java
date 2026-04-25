package org.pwr.cloud.lab.packing.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public record CreatePackingTaskCommand(OrderId orderId) implements Command<Void> {}
