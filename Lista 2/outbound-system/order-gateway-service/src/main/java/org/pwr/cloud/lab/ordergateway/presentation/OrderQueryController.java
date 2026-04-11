package org.pwr.cloud.lab.ordergateway.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.ordergateway.application.OrderService;
import org.pwr.cloud.lab.ordergateway.domain.OrderStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderQueryController {
    private final OrderService orderService;

    @GetMapping("/reports/{orderId}")
    public OrderReportDto getReport(@PathVariable @Valid OrderId orderId) {
        return orderService.getOrderReport(orderId);
    }

    public record OrderReportDto(
            OrderId orderId,
            OrderStatus status,
            List<String> products,
            Map<String, String> details,
            Instant updatedAt) {}
}
