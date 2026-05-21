package org.pwr.cloud.lab.reservation.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.reservation.api.dto.ProductDto;

public record AddProductCommand(String name) implements Command<ProductDto> {}
