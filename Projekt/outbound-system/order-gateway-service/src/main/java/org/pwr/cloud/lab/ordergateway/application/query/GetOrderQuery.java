package org.pwr.cloud.lab.ordergateway.application.query;

import org.pwr.cloud.lab.common.application.cqs.Query;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderReportDto;

public record GetOrderQuery(OrderId orderId) implements Query<OrderReportDto> {}
