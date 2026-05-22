package org.pwr.cloud.lab.bff.infrastructure.proxy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.pwr.cloud.lab.bff.api.dto.packing.FinishPackingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.pwr.cloud.lab.bff.infrastructure.proxy.PackingClient.PACKING_SERVICE;

@Retry(name = PACKING_SERVICE)
@FeignClient(name = PACKING_SERVICE, url = "${services.packing.url}")
public interface PackingClient {

    String PACKING_SERVICE = "packing-service";

    @CircuitBreaker(name = PACKING_SERVICE)
    @PostMapping(value = "/api/packing/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String finishPacking(@PathVariable String orderId, @RequestBody FinishPackingRequest request);
}
