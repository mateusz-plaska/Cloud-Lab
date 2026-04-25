package org.pwr.cloud.lab.ordergateway.application.command;

import org.pwr.cloud.lab.common.application.cqs.Command;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderItemDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateOrderCommand(CustomerId customerId, List<OrderItemDto> items, MultipartFile file)
        implements Command<OrderId> {}
