package org.pwr.cloud.lab.ordergateway.application.query;

import org.pwr.cloud.lab.common.application.cqs.Query;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderListItemDto;

import java.util.List;

public record GetOrdersQuery(CustomerId customerId) implements Query<List<OrderListItemDto>> {

    public static GetOrdersQuery all() {
        return new GetOrdersQuery(null);
    }

    public static GetOrdersQuery forCustomer(CustomerId customerId) {
        return new GetOrdersQuery(customerId);
    }
}