package org.pwr.cloud.lab.ordergateway.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderFileDto;
import org.pwr.cloud.lab.ordergateway.application.query.GetOrderFileQuery;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.pwr.cloud.lab.ordergateway.domain.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderFileQueryHandler implements QueryHandler<GetOrderFileQuery, OrderFileDto> {
    private final StorageService storageService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderFileDto handle(GetOrderFileQuery query) {
        var order = orderRepository.findById(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));

        var storageFileKey = order.metadata().get("file_storage_key");
        var filename = order.metadata().get("filename");

        if (storageFileKey == null) {
            throw new IllegalStateException("No file attached to the order [" + order.orderId() + "]");
        }

        var fileBytes = storageService.loadFile(storageFileKey);
        return new OrderFileDto(fileBytes, filename);
    }
}
