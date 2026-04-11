package org.pwr.cloud.lab.ordergateway.application;

import org.pwr.cloud.lab.ordergateway.domain.OrderItem;
import org.pwr.cloud.lab.ordergateway.presentation.OrderController;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemConverter {

    public List<OrderItem> convert(List<OrderController.OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream().map(this::convert).toList();
    }

    private OrderItem convert(OrderController.OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .productId(orderItemDto.productId())
                .quantity(orderItemDto.quantity())
                .build();
    }
}
