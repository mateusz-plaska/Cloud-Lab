package org.pwr.cloud.lab.bff.infrastructure.proxy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.pwr.cloud.lab.bff.infrastructure.proxy.ShippingClient.SHIPPING_SERVICE;

@Retry(name = SHIPPING_SERVICE)
@FeignClient(name = SHIPPING_SERVICE, url = "${services.shipping.url}")
public interface ShippingClient {

    String SHIPPING_SERVICE = "shipping-service";

    @CircuitBreaker(name = SHIPPING_SERVICE)
    @GetMapping(value = "/api/shipments/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    String getShipment(@PathVariable String orderId);
}
