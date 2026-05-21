package org.pwr.cloud.lab.bff.api.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.packing.FinishPackingRequest;
import org.pwr.cloud.lab.bff.domain.model.Roles;
import org.pwr.cloud.lab.bff.infrastructure.proxy.PackingServiceProxy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packing")
@RequiredArgsConstructor
public class PackingController {

    private final PackingServiceProxy packingServiceProxy;

    @RolesAllowed({Roles.OPERATOR, Roles.ADMIN})
    @PostMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> finishPacking(
            @PathVariable String orderId, @RequestBody @Valid FinishPackingRequest request) {
        return ResponseEntity.ok(packingServiceProxy.finishPacking(orderId, request));
    }
}