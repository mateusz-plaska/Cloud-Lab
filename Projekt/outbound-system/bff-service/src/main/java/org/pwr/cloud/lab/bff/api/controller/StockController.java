package org.pwr.cloud.lab.bff.api.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.infrastructure.proxy.ReservationServiceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Validated
public class StockController {

    private final ReservationServiceProxy reservationServiceProxy;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStocks() {
        return ResponseEntity.ok(reservationServiceProxy.getStocks());
    }

    @PostMapping
    public ResponseEntity<Void> addStock(
            @RequestParam @NotBlank String productId,
            @RequestParam @Min(1) @Max(1000) int quantity) {
        reservationServiceProxy.addStock(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}