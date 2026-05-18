package org.pwr.cloud.lab.bff.api.dto.sse;

import java.time.Instant;

public record OrderStatusUpdate(String orderId, String eventType, String station, Instant timestamp) {}