package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.infrastructure.proxy.PickingServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picking")
@RequiredArgsConstructor
public class PickingController {

    private final PickingServiceProxy pickingServiceProxy;

    @PostMapping("/{orderId}/pick")
    public ResponseEntity<String> pickItem(
            @PathVariable String orderId,
            @RequestParam String productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(pickingServiceProxy.pickItem(orderId, productId, quantity));
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<String> failItem(
            @PathVariable String orderId,
            @RequestParam String productId,
            @RequestParam String reason) {
        return ResponseEntity.ok(pickingServiceProxy.failItem(orderId, productId, reason));
    }
}