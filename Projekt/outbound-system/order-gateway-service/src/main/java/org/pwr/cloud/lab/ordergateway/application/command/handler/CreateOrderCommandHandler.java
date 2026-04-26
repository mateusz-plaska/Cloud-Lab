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
import org.pwr.cloud.lab.ordergateway.domain.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, OrderId> {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderItemConverter orderItemConverter;
    private final FileMetadataParser fileParser;
    private final StorageService storageService;

    @Override
    @Transactional
    public OrderId handle(CreateOrderCommand command) {
        var orderId = OrderId.newInstance();
        var metadata = parseMetadata(command.file(), orderId);
        var items = orderItemConverter.convert(command.items());
        var order = Order.builder()
                .orderId(orderId)
                .customerId(command.customerId())
                .status(OrderStatus.PLANNED)
                .items(items)
                .metadata(metadata)
                .build();

        orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(order.orderId(), order.items(), order.metadata());
        return order.orderId();
    }

    private Map<String, String> parseMetadata(MultipartFile file, OrderId orderId) {
        var metadata = fileParser.parse(file);
        metadata.put("filename", file.getOriginalFilename());
        metadata.put("file_size_bytes", String.valueOf(file.getSize()));
        metadata.put("upload_date", Instant.now().toString());

        var storageObjectKey = storageService.uploadFile(file, orderId);
        metadata.put("file_storage_key", storageObjectKey);

        return metadata;
    }
}
