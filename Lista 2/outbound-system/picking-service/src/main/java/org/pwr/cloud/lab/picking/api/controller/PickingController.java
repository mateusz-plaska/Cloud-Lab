package org.pwr.cloud.lab.picking.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.picking.application.service.PickingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picking")
@RequiredArgsConstructor
public class PickingController {
    private final PickingService pickingService;

    @PostMapping("/{orderId}/pick")
    public ResponseEntity<String> pickItem(
            @PathVariable @Valid OrderId orderId,
            @RequestParam @Valid ProductId productId,
            @RequestParam(defaultValue = "1") @Min(1) @Max(100) Integer quantity) {
        pickingService.pickItem(orderId, productId, quantity);
        return ResponseEntity.ok("Item picked successfully");
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<String> failItem(
            @PathVariable @Valid OrderId orderId,
            @RequestParam @Valid ProductId productId,
            @RequestParam String reason) {
        pickingService.reportMissingItem(orderId, productId, reason);
        return ResponseEntity.ok("Reported missing item successfully");
    }
}
