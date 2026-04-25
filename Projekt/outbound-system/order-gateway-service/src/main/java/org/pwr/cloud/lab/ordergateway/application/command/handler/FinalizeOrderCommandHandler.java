package org.pwr.cloud.lab.ordergateway.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.ordergateway.application.command.FinalizeOrderCommand;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.notification.Notifier;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinalizeOrderCommandHandler implements CommandHandler<FinalizeOrderCommand, Void> {
    private final OrderRepository orderRepository;
    private final Notifier notifier;

    @Override
    @Transactional
    public Void handle(FinalizeOrderCommand command) {
        var order = orderRepository
                .findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        var updatedOrder = order.finalizeOrder(command.trackingNumber());
        orderRepository.save(updatedOrder);
        notifier.notifyOrderUpdate(
                command.orderId(),
                "Order has been shipped! Tracking: " + command.trackingNumber().value());

        return null;
    }
}
