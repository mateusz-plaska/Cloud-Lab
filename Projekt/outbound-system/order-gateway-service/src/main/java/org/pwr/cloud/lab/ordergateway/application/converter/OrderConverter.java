package org.pwr.cloud.lab.ordergateway.application.converter;

import org.pwr.cloud.lab.ordergateway.api.dto.OrderListItemDto;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderReportDto;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public OrderListItemDto toOrderListItemDto(Order order) {
        return new OrderListItemDto(
                order.orderId(),
                order.customerId(),
                order.status(),
                order.items() != null ? order.items().size() : 0,
                order.createdAt(),
                order.updatedAt());
    }

    public OrderReportDto toOrderReportDto(Order order) {
        return new OrderReportDto(
                order.orderId(),
                order.status(),
                order.items().stream()
                        .map(i -> i.productId() + " (x" + i.quantity() + ")")
                        .toList(),
                order.metadata(),
                order.updatedAt());
    }
}
