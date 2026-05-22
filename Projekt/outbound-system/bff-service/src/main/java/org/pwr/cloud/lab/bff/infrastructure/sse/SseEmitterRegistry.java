package org.pwr.cloud.lab.bff.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.domain.repository.OrderStatusUpdateRepository;
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

    private final OrderStatusUpdateRepository statusUpdateRepository;

    private final JsonMapper jsonMapper;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<SseEmitter> dashboardEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribeDashboard() {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        dashboardEmitters.add(emitter);

        emitter.onCompletion(() -> dashboardEmitters.remove(emitter));
        emitter.onTimeout(() -> dashboardEmitters.remove(emitter));
        emitter.onError(e -> dashboardEmitters.remove(emitter));

        log.info("New SSE dashboard subscriber, total: {}", dashboardEmitters.size());
        return emitter;
    }

    public SseEmitter subscribe(String orderId) {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        var orderEmitterList = emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>());
        orderEmitterList.add(emitter);

        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(e -> remove(orderId, emitter));

        var pastUpdates = statusUpdateRepository.findAllByOrderIdOrderedByTimestampAsc(orderId);
        for (var update : pastUpdates) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.ORDER_UPDATE).data(toJson(update)));
            } catch (IOException e) {
                break;
            }
        }

        log.info("New SSE subscriber for order [{}], replayed {} events, total subscribers: {}",
                orderId, pastUpdates.size(), orderEmitterList.size());
        return emitter;
    }

    public void broadcast(String orderId, OrderStatusUpdate update) {
        statusUpdateRepository.save(update);

        var orderEmitters = emitters.getOrDefault(orderId, new CopyOnWriteArrayList<>());
        List<SseEmitter> dead = new ArrayList<>();
        for (var emitter : orderEmitters) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.ORDER_UPDATE).data(toJson(update)));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }

        orderEmitters.removeAll(dead);
        notifyDashboard(update);
    }

    private void notifyDashboard(OrderStatusUpdate update) {
        List<SseEmitter> dead = new ArrayList<>();
        for (var emitter : dashboardEmitters) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.DASHBOARD_UPDATE).data(toJson(update)));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        dashboardEmitters.removeAll(dead);
    }

    private void remove(String orderId, SseEmitter emitter) {
        var orderEmitters = emitters.get(orderId);
        if (orderEmitters != null) {
            orderEmitters.remove(emitter);
        }
    }

    private String toJson(OrderStatusUpdate update) {
        try {
            return jsonMapper.writeValueAsString(update);
        } catch (Exception e) {
            log.error("Failed to serialize SSE update for order [{}]: {}", update.orderId(), e.getMessage());
            return "{}";
        }
    }
}