package org.pwr.cloud.lab.packing.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.api.dto.FinishPackingRequestDto;
import org.pwr.cloud.lab.packing.application.service.PackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packing")
@RequiredArgsConstructor
public class PackingController {
    private final PackingService packingService;

    @PostMapping("/{orderId}")
    public ResponseEntity<String> finishPacking(
            @PathVariable OrderId orderId, @RequestBody @Valid FinishPackingRequestDto request) {
        packingService.finishPacking(orderId, request.boxSize(), request.weight());
        return ResponseEntity.ok("Order packed successfully");
    }
}
