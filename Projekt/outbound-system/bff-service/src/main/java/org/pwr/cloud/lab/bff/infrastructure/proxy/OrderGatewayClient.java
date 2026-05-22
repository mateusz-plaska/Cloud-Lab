package org.pwr.cloud.lab.bff.infrastructure.proxy;

import feign.form.FormData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

import static org.pwr.cloud.lab.bff.infrastructure.proxy.OrderGatewayClient.ORDER_GATEWAY_SERVICE;

@Retry(name = ORDER_GATEWAY_SERVICE)
@FeignClient(name = ORDER_GATEWAY_SERVICE, url = "${services.order-gateway.url}")
public interface OrderGatewayClient {

    String ORDER_GATEWAY_SERVICE = "order-gateway";

    @CircuitBreaker(name = ORDER_GATEWAY_SERVICE)
    @GetMapping("/api/orders")
    String getOrders(@RequestParam(required = false) String customerId);

    @CircuitBreaker(name = ORDER_GATEWAY_SERVICE)
    @GetMapping("/api/orders/reports/{id}")
    String getOrderReport(@PathVariable String id);

    @CircuitBreaker(name = ORDER_GATEWAY_SERVICE)
    @PostMapping(value = "/api/orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String createOrder(@RequestPart("data") DataPart data, @RequestPart("file") FormData file);

    record DataPart(String customerId, List<Item> items) {
        record Item(String productId, int quantity) {}
    }
}
