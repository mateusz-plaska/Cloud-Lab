package org.pwr.cloud.lab.packing.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.api.dto.FinishPackingRequestDto;
import org.pwr.cloud.lab.packing.application.command.FinishPackingCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packing")
@RequiredArgsConstructor
public class PackingController {
    private final Mediator mediator;

    @PostMapping("/{orderId}")
    public ResponseEntity<String> finishPacking(
            @PathVariable OrderId orderId, @RequestBody @Valid FinishPackingRequestDto request) {
        mediator.send(new FinishPackingCommand(orderId, request.boxSize(), request.weight()));
        return ResponseEntity.ok("Order packed successfully");
    }
}
