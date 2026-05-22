package org.pwr.cloud.lab.bff.api.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.product.CreateProductRequest;
import org.pwr.cloud.lab.bff.domain.model.Roles;
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

    @RolesAllowed({Roles.OPERATOR, Roles.ADMIN})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationServiceProxy.createProduct(request.name()));
    }
}
