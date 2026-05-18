package org.pwr.cloud.lab.ordergateway.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderListItemDto;
import org.pwr.cloud.lab.ordergateway.application.converter.OrderConverter;
import org.pwr.cloud.lab.ordergateway.application.query.GetOrdersQuery;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetOrdersQueryHandler implements QueryHandler<GetOrdersQuery, List<OrderListItemDto>> {

    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;

    @Override
    public List<OrderListItemDto> handle(GetOrdersQuery query) {
        var orders = query.customerId() != null
                ? orderRepository.findByCustomerId(query.customerId())
                : orderRepository.findAll();
        return orders.stream().map(orderConverter::toOrderListItemDto).toList();
    }
}