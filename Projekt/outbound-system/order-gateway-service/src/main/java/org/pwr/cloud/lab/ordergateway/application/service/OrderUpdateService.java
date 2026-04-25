package org.pwr.cloud.lab.ordergateway.application.service;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;
import org.pwr.cloud.lab.ordergateway.domain.notification.Notifier;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderUpdateService {
    private final OrderRepository orderRepository;
    private final Notifier notifier;

    public void updateStatus(OrderId orderId, OrderStatus newStatus, String reason) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        var updatedOrder = order.updateStatus(newStatus, reason);
        orderRepository.save(updatedOrder);

        if (reason != null) {
            notifier.notifyOrderUpdate(orderId, reason);
        }
    }

    public void finalizeOrder(OrderId orderId, TrackingNumber trackingNumber) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        var updatedOrder = order.finalizeOrder(trackingNumber);
        orderRepository.save(updatedOrder);
        notifier.notifyOrderUpdate(orderId, "Order has been shipped! Tracking: " + trackingNumber.value());
    }
}
