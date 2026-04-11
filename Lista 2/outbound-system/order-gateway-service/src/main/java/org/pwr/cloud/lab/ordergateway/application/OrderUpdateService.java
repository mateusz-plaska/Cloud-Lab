package org.pwr.cloud.lab.ordergateway.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.OrderRepository;
import org.pwr.cloud.lab.ordergateway.domain.OrderStatus;
import org.pwr.cloud.lab.ordergateway.infrastructure.WebhookPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderUpdateService {
    private final OrderRepository orderRepository;
    private final WebhookPublisher webhookPublisher;

    public void updateStatus(OrderId orderId, OrderStatus newStatus, String reason) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        order = order.toBuilder().status(newStatus).build();
        if (reason != null) order.metadata().put("status_" + newStatus + "_reason", reason);

        orderRepository.save(order);

        if (reason != null) webhookPublisher.sendWebhookNotification(orderId, reason);
    }

    public void finalizeOrder(OrderId orderId, String trackingNumber) {
        var order = orderRepository.findById(orderId).orElseThrow();
        order = order.toBuilder().status(OrderStatus.READY).build();
        order.metadata().put("tracking_number", trackingNumber);
        orderRepository.save(order);
        webhookPublisher.sendWebhookNotification(orderId, "Order has been shipped! Tracking: " + trackingNumber);
    }
}
