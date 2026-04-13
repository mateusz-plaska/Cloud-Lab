package org.pwr.cloud.lab.shipping.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.application.ShipmentService;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentQueryController {
    private final ShipmentService shipmentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Shipment> getShipment(@PathVariable @Valid OrderId orderId) {
        return ResponseEntity.ok(shipmentService.getShipment(orderId));
    }
}
