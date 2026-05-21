package org.pwr.cloud.lab.bff.api.dto.sse;

import java.time.Instant;

public record OrderStatusUpdate(String orderId, SseEventType eventType, String station, Instant timestamp) {}