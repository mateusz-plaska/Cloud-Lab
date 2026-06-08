package org.pwr.cloud.lab.bff.api.dto.dashboard;

import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;

import java.util.List;
import java.util.Map;

public record ThroughputResponse(long bucketMs, List<Point> points) {

    public record Point(long timestamp, Map<SseEventType, Long> counts) {}
}
