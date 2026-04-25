package org.pwr.cloud.lab.ordergateway.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.ordergateway.application.command.UpdateOrderStatusCommand;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.notification.Notifier;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusCommandHandler implements CommandHandler<UpdateOrderStatusCommand, Void> {
    private final OrderRepository orderRepository;
    private final Notifier notifier;

    @Override
    @Transactional
    public Void handle(UpdateOrderStatusCommand command) {
        var order = orderRepository
                .findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        var updatedOrder = order.updateStatus(command.orderStatus(), command.reason());
        orderRepository.save(updatedOrder);

        if (command.reason() != null) {
            notifier.notifyOrderUpdate(command.orderId(), command.reason());
        }

        return null;
    }
}
