package org.pwr.cloud.lab.shipping.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public record CreateShipmentCommand(OrderId orderId, double weight, BoxType boxType) implements Command<Void> {}
