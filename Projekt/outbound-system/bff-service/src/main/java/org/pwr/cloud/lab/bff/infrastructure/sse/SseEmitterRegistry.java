package org.pwr.cloud.lab.bff.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseEmitterRegistry {

    private final JsonMapper jsonMapper;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String orderId) {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(e -> remove(orderId, emitter));

        log.info("New SSE subscriber for order [{}], total: {}", orderId, emitters.get(orderId).size());
        return emitter;
    }

    public void broadcast(String orderId, OrderStatusUpdate update) {
        var orderEmitters = emitters.getOrDefault(orderId, new CopyOnWriteArrayList<>());
        List<SseEmitter> dead = new ArrayList<>();

        for (SseEmitter emitter : orderEmitters) {
            try {
                emitter.send(SseEmitter.event().name("order-update").data(jsonMapper.writeValueAsString(update)));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }

        orderEmitters.removeAll(dead);
    }

    private void remove(String orderId, SseEmitter emitter) {
        var orderEmitters = emitters.get(orderId);
        if (orderEmitters != null) {
            orderEmitters.remove(emitter);
        }
    }
}