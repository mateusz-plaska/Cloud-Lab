package org.pwr.cloud.lab.ordergateway.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.application.command.CreateOrderCommand;
import org.pwr.cloud.lab.ordergateway.application.converter.OrderItemConverter;
import org.pwr.cloud.lab.ordergateway.application.parser.FileMetadataParser;
import org.pwr.cloud.lab.ordergateway.domain.messaging.OrderEventPublisher;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, OrderId> {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderItemConverter orderItemConverter;
    private final FileMetadataParser fileParser;

    @Override
    @Transactional
    public OrderId handle(CreateOrderCommand command) {
        var metadata = fileParser.parse(command.file());
        metadata.put("_filename", command.file().getOriginalFilename());

        var items = orderItemConverter.convert(command.items());
        var order = Order.builder()
                .orderId(OrderId.newInstance())
                .customerId(command.customerId())
                .status(OrderStatus.PLANNED)
                .items(items)
                .metadata(metadata)
                .build();

        orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(order.orderId(), order.items(), order.metadata());
        return order.orderId();
    }
}
