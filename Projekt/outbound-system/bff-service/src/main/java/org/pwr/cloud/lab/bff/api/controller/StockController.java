package org.pwr.cloud.lab.bff.api.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.stock.AddStockRequest;
import org.pwr.cloud.lab.bff.domain.model.Roles;
import org.pwr.cloud.lab.bff.infrastructure.proxy.ReservationServiceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final ReservationServiceProxy reservationServiceProxy;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStocks() {
        return ResponseEntity.ok(reservationServiceProxy.getStocks());
    }

    @RolesAllowed({Roles.OPERATOR, Roles.ADMIN})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addStock(@RequestBody @Valid AddStockRequest request) {
        reservationServiceProxy.addStock(request.productId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
