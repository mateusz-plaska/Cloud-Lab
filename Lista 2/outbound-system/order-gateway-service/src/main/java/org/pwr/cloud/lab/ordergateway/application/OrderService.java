package org.pwr.cloud.lab.ordergateway.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.CustomerId;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.Order;
import org.pwr.cloud.lab.ordergateway.domain.OrderItem;
import org.pwr.cloud.lab.ordergateway.domain.OrderRepository;
import org.pwr.cloud.lab.ordergateway.domain.OrderStatus;
import org.pwr.cloud.lab.ordergateway.infrastructure.RabbitMqService;
import org.pwr.cloud.lab.ordergateway.presentation.OrderQueryController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitMqService rabbitMqService;

    public OrderId createOrder(CustomerId customerId, List<OrderItem> items, Map<String, String> metadata) {
        var order = Order.builder()
                .orderId(OrderId.newInstance())
                .customerId(customerId)
                .status(OrderStatus.PLANNED)
                .items(items)
                .metadata(metadata)
                .build();

        orderRepository.save(order);
        rabbitMqService.sendOrderCreatedEvent(order.orderId(), order.items(), metadata);
        return order.orderId();
    }

    public OrderQueryController.OrderReportDto getOrderReport(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderQueryController.OrderReportDto(
                order.orderId(),
                order.status(),
                order.items().stream()
                        .map(i -> i.productId() + " (x" + i.quantity() + ")")
                        .toList(),
                order.metadata(),
                order.updatedAt());
    }
}
