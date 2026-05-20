package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.infrastructure.proxy.ReservationServiceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ReservationServiceProxy reservationServiceProxy;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProducts() {
        return ResponseEntity.ok(reservationServiceProxy.getProducts());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProduct(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationServiceProxy.createProduct(name));
    }
}