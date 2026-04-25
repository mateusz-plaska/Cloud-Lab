package org.pwr.cloud.lab.ordergateway.api.converter;

import org.pwr.cloud.lab.ordergateway.api.dto.OrderItemDto;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemConverter {

    public List<OrderItem> convert(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream().map(this::convert).toList();
    }

    private OrderItem convert(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .productId(orderItemDto.productId())
                .quantity(orderItemDto.quantity())
                .build();
    }
}
