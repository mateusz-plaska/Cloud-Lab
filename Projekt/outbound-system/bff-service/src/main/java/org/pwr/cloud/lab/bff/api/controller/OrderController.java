package org.pwr.cloud.lab.bff.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.order.CreateOrderRequest;
import org.pwr.cloud.lab.bff.application.user.CurrentUserService;
import org.pwr.cloud.lab.bff.infrastructure.proxy.OrderServiceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceProxy orderServiceProxy;
    private final CurrentUserService currentUserService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrders() {
        String customerId = currentUserService.isUser()
                ? currentUserService.getCurrentUser().id().toCustomerId().value()
                : null;
        return ResponseEntity.ok(orderServiceProxy.getOrders(customerId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        String result = orderServiceProxy.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderReport(@PathVariable String orderId) {
        return ResponseEntity.ok(orderServiceProxy.getOrderReport(orderId));
    }
}