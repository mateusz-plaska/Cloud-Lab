package org.pwr.cloud.lab.ordergateway.api.dto;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record OrderReportDto(
        OrderId orderId, OrderStatus status, List<String> products, Map<String, String> details, Instant updatedAt) {}
