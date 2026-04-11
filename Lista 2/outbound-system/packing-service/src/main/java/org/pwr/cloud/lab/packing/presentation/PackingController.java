package org.pwr.cloud.lab.packing.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.packing.application.PackingService;
import org.pwr.cloud.lab.packing.domain.BoxSize;
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

    public record FinishPackingRequestDto(
            @NotNull BoxSize boxSize, @NotNull @Positive Double weight) {}
}
