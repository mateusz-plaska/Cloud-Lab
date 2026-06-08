package org.pwr.cloud.lab.bff.api.dto.dashboard;

import java.util.List;
import java.util.Map;

public record LeadTimeResponse(long bucketMs, List<Point> points) {

    public record Point(
            long timestamp, long completedOrders, Map<String, Double> avgStageSeconds, double p95TotalSeconds) {}
}
