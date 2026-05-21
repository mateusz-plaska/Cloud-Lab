package org.pwr.cloud.lab.bff.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseEmitterRegistry {

    private static final int MAX_HISTORY = 50;

    private final JsonMapper jsonMapper;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<OrderStatusUpdate>> history = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<SseEmitter> dashboardEmitters = new CopyOnWriteArrayList<>();
    private final Deque<String> dashboardHistory = new ConcurrentLinkedDeque<>();

    public SseEmitter subscribeDashboard() {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        dashboardEmitters.add(emitter);

        emitter.onCompletion(() -> dashboardEmitters.remove(emitter));
        emitter.onTimeout(() -> dashboardEmitters.remove(emitter));
        emitter.onError(e -> dashboardEmitters.remove(emitter));

        for (String json : dashboardHistory) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.DASHBOARD_UPDATE).data(json));
            } catch (IOException e) {
                break;
            }
        }

        log.info("New SSE dashboard subscriber, replayed {} events, total: {}", dashboardHistory.size(), dashboardEmitters.size());
        return emitter;
    }

    public SseEmitter subscribe(String orderId) {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        var orderEmitterList = emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>());
        orderEmitterList.add(emitter);

        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(e -> remove(orderId, emitter));

        var past = history.getOrDefault(orderId, new ConcurrentLinkedDeque<>());
        for (OrderStatusUpdate update : past) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.ORDER_UPDATE).data(jsonMapper.writeValueAsString(update)));
            } catch (IOException e) {
                break;
            }
        }

        log.info("New SSE subscriber for order [{}], replayed {} events, total subscribers: {}",
                orderId, past.size(), orderEmitterList.size());
        return emitter;
    }

    public void broadcast(String orderId, OrderStatusUpdate update) {
        Deque<OrderStatusUpdate> orderHistory = history.computeIfAbsent(orderId, k -> new ConcurrentLinkedDeque<>());
        orderHistory.addLast(update);
        while (orderHistory.size() > MAX_HISTORY) {
            orderHistory.pollFirst();
        }

        var orderEmitters = emitters.getOrDefault(orderId, new CopyOnWriteArrayList<>());
        List<SseEmitter> dead = new ArrayList<>();

        String json;
        try {
            json = jsonMapper.writeValueAsString(update);
        } catch (Exception e) {
            log.error("Failed to serialize SSE update for order [{}]: {}", orderId, e.getMessage());
            return;
        }

        for (SseEmitter emitter : orderEmitters) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.ORDER_UPDATE).data(json));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }

        orderEmitters.removeAll(dead);
        notifyDashboard(json);
    }

    private void notifyDashboard(String json) {
        dashboardHistory.addLast(json);
        while (dashboardHistory.size() > MAX_HISTORY) {
            dashboardHistory.pollFirst();
        }

        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : dashboardEmitters) {
            try {
                emitter.send(SseEmitter.event().name(SseEventNames.DASHBOARD_UPDATE).data(json));
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
}