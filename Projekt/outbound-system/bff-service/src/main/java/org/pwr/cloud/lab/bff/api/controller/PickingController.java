package org.pwr.cloud.lab.bff.api.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.domain.model.Roles;
import org.pwr.cloud.lab.bff.infrastructure.proxy.PickingServiceProxy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picking")
@RequiredArgsConstructor
public class PickingController {

    private final PickingServiceProxy pickingServiceProxy;

    @RolesAllowed({Roles.OPERATOR, Roles.ADMIN})
    @PostMapping(value = "/{orderId}/pick", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> pickItem(
            @PathVariable String orderId,
            @RequestParam String productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(pickingServiceProxy.pickItem(orderId, productId, quantity));
    }

    @RolesAllowed({Roles.OPERATOR, Roles.ADMIN})
    @PostMapping(value = "/{orderId}/fail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> failItem(
            @PathVariable String orderId,
            @RequestParam String productId,
            @RequestParam String reason) {
        return ResponseEntity.ok(pickingServiceProxy.failItem(orderId, productId, reason));
    }
}