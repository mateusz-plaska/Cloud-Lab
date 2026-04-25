package org.pwr.cloud.lab.ordergateway.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderReportDto;
import org.pwr.cloud.lab.ordergateway.application.converter.OrderConverter;
import org.pwr.cloud.lab.ordergateway.application.query.GetOrderQuery;
import org.pwr.cloud.lab.ordergateway.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderQueryHandler implements QueryHandler<GetOrderQuery, OrderReportDto> {
    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;

    @Override
    @Transactional(readOnly = true)
    public OrderReportDto handle(GetOrderQuery query) {
        return orderRepository
                .findById(query.orderId())
                .map(orderConverter::toOrderReportDto)
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
    }
}
