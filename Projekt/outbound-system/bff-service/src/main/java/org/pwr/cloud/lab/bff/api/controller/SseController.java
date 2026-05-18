package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.infrastructure.sse.SseEmitterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping(value = "/orders/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrderStatus(@PathVariable String orderId) {
        return sseEmitterRegistry.subscribe(orderId);
    }
}