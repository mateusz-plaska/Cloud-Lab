package org.pwr.cloud.lab.bff.infrastructure.proxy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.pwr.cloud.lab.bff.infrastructure.proxy.PickingClient.PICKING_SERVICE;

@Retry(name = PICKING_SERVICE)
@FeignClient(name = PICKING_SERVICE, url = "${services.picking.url}")
public interface PickingClient {

    String PICKING_SERVICE = "picking-service";

    @CircuitBreaker(name = PICKING_SERVICE)
    @PostMapping(value = "/api/picking/{orderId}/pick", produces = MediaType.APPLICATION_JSON_VALUE)
    String pickItem(@PathVariable String orderId, @RequestParam String productId, @RequestParam(defaultValue = "1") int quantity);

    @CircuitBreaker(name = PICKING_SERVICE)
    @PostMapping(value = "/api/picking/{orderId}/fail", produces = MediaType.APPLICATION_JSON_VALUE)
    String failItem(@PathVariable String orderId, @RequestParam String productId, @RequestParam String reason);
}
