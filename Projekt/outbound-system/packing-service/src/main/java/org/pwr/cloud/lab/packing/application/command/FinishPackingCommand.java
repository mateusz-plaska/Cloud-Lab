package org.pwr.cloud.lab.packing.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public record FinishPackingCommand(OrderId orderId, BoxSize boxSize, double weight) implements Command<Void> {}
