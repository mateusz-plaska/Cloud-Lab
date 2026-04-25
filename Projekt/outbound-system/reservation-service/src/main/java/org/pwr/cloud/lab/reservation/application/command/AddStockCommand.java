package org.pwr.cloud.lab.reservation.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record AddStockCommand(ProductId productId, Integer quantity) implements Command<Void> {}
