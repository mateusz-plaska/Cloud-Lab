package org.pwr.cloud.lab.ordergateway.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.api.converter.OrderConverter;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderReportDto;
import org.pwr.cloud.lab.ordergateway.application.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderQueryController {
    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @GetMapping("/reports/{orderId}")
    public ResponseEntity<OrderReportDto> getReport(@PathVariable @Valid OrderId orderId) {
        var order = orderService.getOrder(orderId);
        var orderReportDto = orderConverter.toOrderReportDto(order);
        return ResponseEntity.ok(orderReportDto);
    }
}
