package org.pwr.cloud.lab.ordergateway.application.service;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.messaging.OrderEventPublisher;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderItem;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public Order createOrder(CustomerId customerId, List<OrderItem> items, Map<String, String> metadata) {
        var order = Order.builder()
                .orderId(OrderId.newInstance())
                .customerId(customerId)
                .status(OrderStatus.PLANNED)
                .items(items)
                .metadata(metadata)
                .build();

        orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(order.orderId(), order.items(), metadata);
        return order;
    }

    public Order getOrder(OrderId orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
