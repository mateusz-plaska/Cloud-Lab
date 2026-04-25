package org.pwr.cloud.lab.ordergateway.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;

public record FinalizeOrderCommand(OrderId orderId, TrackingNumber trackingNumber) implements Command<Void> {}
