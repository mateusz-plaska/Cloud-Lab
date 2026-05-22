package org.pwr.cloud.lab.bff.infrastructure.proxy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.pwr.cloud.lab.bff.infrastructure.proxy.ReservationClient.RESERVATION_SERVICE;

@Retry(name = RESERVATION_SERVICE)
@FeignClient(name = RESERVATION_SERVICE, url = "${services.reservation.url}")
public interface ReservationClient {

    String RESERVATION_SERVICE = "reservation-service";

    @CircuitBreaker(name = RESERVATION_SERVICE)
    @GetMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
    String getProducts();

    @CircuitBreaker(name = RESERVATION_SERVICE)
    @PostMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
    String createProduct(@RequestParam String name);

    @CircuitBreaker(name = RESERVATION_SERVICE)
    @GetMapping(value = "/api/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
    String getStocks();

    @CircuitBreaker(name = RESERVATION_SERVICE)
    @PostMapping("/api/stocks")
    void addStock(@RequestParam String productId, @RequestParam int quantity);
}
