package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.infrastructure.proxy.ShippingServiceProxy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShippingServiceProxy shippingServiceProxy;

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShipment(@PathVariable String orderId) {
        return ResponseEntity.ok(shippingServiceProxy.getShipment(orderId));
    }
}