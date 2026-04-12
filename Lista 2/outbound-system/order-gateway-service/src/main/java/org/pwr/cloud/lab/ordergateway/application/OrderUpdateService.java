package org.pwr.cloud.lab.ordergateway.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;
import org.pwr.cloud.lab.common.exception.OrderNotFoundException;
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
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.status().ordinal() >= newStatus.ordinal()) return;

        order = order.toBuilder().status(newStatus).build();
        if (reason != null) order.metadata().put("status_" + newStatus + "_reason", reason);

        orderRepository.save(order);

        if (reason != null) webhookPublisher.sendWebhookNotification(orderId, reason);
    }

    public void finalizeOrder(OrderId orderId, TrackingNumber trackingNumber) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        var updatedOrder = order.toBuilder().status(OrderStatus.READY).build();
        updatedOrder.metadata().put("tracking_number", trackingNumber.value());
        orderRepository.save(updatedOrder);
        webhookPublisher.sendWebhookNotification(
                orderId, "Order has been shipped! Tracking: " + trackingNumber.value());
    }
}
