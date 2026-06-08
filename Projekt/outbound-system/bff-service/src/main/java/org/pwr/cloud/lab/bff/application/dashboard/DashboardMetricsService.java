package org.pwr.cloud.lab.bff.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.dashboard.LeadTimeResponse;
import org.pwr.cloud.lab.bff.api.dto.dashboard.ThroughputResponse;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;
import org.pwr.cloud.lab.bff.domain.repository.OrderStatusUpdateRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class
DashboardMetricsService {

    private static final List<String> STAGES = List.of("RESERVATION", "PICKING", "PACKING", "SHIPPING");
    private static final int MAX_BUCKETS = 500;

    private final OrderStatusUpdateRepository repository;

    public Duration resolveBucket(Instant from, Instant to, Long manualBucketMs) {
        var range = Duration.between(from, to);
        if (range.isZero() || range.isNegative()) {
            throw new IllegalArgumentException("Time range must be positive");
        }
        if (manualBucketMs == null) {
            return autoBucketWidth(range);
        }
        if (manualBucketMs <= 0) {
            throw new IllegalArgumentException("Bucket size must be positive");
        }
        long count = Math.floorDiv(range.toMillis() + manualBucketMs - 1, manualBucketMs);
        if (count > MAX_BUCKETS) {
            throw new IllegalArgumentException("Selected granularity is too fine for this range (%d buckets, max %d)"
                    .formatted(count, MAX_BUCKETS));
        }
        return Duration.ofMillis(manualBucketMs);
    }

    public ThroughputResponse throughput(Instant from, Instant to, ZoneId zone, Duration bucket) {
        var bucketStarts = bucketStarts(from, to, bucket, zone);

        Map<Long, Map<SseEventType, Long>> byBucket = new LinkedHashMap<>();
        for (var start : bucketStarts) {
            byBucket.put(start, new EnumMap<>(SseEventType.class));
        }

        for (var event : repository.findByTimestampBetween(from, to)) {
            var start = bucketStart(event.timestamp(), bucket, zone);
            var counts = byBucket.get(start);
            if (counts != null) {
                counts.merge(event.eventType(), 1L, Long::sum);
            }
        }

        var points = bucketStarts.stream()
                .map(start -> new ThroughputResponse.Point(start, byBucket.get(start)))
                .toList();
        return new ThroughputResponse(bucket.toMillis(), points);
    }

    public LeadTimeResponse leadTime(Instant from, Instant to, ZoneId zone, Duration bucket) {
        var bucketStarts = bucketStarts(from, to, bucket, zone);

        var activeOrderIds = repository.findByTimestampBetween(from, to).stream()
                .map(OrderStatusUpdate::orderId)
                .collect(Collectors.toSet());

        Map<String, EnumMap<SseEventType, Instant>> firstEventTs = new HashMap<>();
        if (!activeOrderIds.isEmpty()) {
            for (var event : repository.findByOrderIdIn(activeOrderIds)) {
                firstEventTs
                        .computeIfAbsent(event.orderId(), k -> new EnumMap<>(SseEventType.class))
                        .merge(event.eventType(), event.timestamp(), (a, b) -> a.isBefore(b) ? a : b);
            }
        }

        Map<Long, StageAccumulator> byBucket = new LinkedHashMap<>();
        for (var start : bucketStarts) {
            byBucket.put(start, new StageAccumulator());
        }

        for (var ts : firstEventTs.values()) {
            var created = ts.get(SseEventType.ORDER_CREATED);
            var reserved = ts.get(SseEventType.STOCK_RESERVED);
            var picked = ts.get(SseEventType.ORDER_PICKED);
            var packed = ts.get(SseEventType.PACKING_FINISHED);
            var shipped = ts.get(SseEventType.SHIPMENT_CREATED);

            addStage(byBucket, bucket, zone, from, to, "RESERVATION", created, reserved);
            addStage(byBucket, bucket, zone, from, to, "PICKING", reserved, picked);
            addStage(byBucket, bucket, zone, from, to, "PACKING", picked, packed);
            addStage(byBucket, bucket, zone, from, to, "SHIPPING", packed, shipped);

            if (created != null && shipped != null && inWindow(shipped, from, to)) {
                var acc = byBucket.get(bucketStart(shipped, bucket, zone));
                var total = durationSeconds(created, shipped);
                if (acc != null && total != null) {
                    acc.addTotal(total);
                }
            }
        }

        var points = bucketStarts.stream()
                .map(start -> byBucket.get(start).toPoint(start))
                .toList();
        return new LeadTimeResponse(bucket.toMillis(), points);
    }

    private static void addStage(
            Map<Long, StageAccumulator> byBucket,
            Duration bucket,
            ZoneId zone,
            Instant from,
            Instant to,
            String stage,
            Instant start,
            Instant end) {
        if (start == null || end == null || !inWindow(end, from, to)) {
            return;
        }
        var seconds = durationSeconds(start, end);
        if (seconds == null) {
            return;
        }
        var acc = byBucket.get(bucketStart(end, bucket, zone));
        if (acc != null) {
            acc.add(stage, seconds);
        }
    }

    private static boolean inWindow(Instant ts, Instant from, Instant to) {
        return !ts.isBefore(from) && !ts.isAfter(to);
    }

    private static Double durationSeconds(Instant from, Instant to) {
        if (from == null || to == null) {
            return null;
        }
        var millis = Duration.between(from, to).toMillis();
        return millis >= 0 ? millis / 1000.0 : null; // fractional seconds keep sub-second stages visible
    }

    private static Duration autoBucketWidth(Duration range) {
        long minutes = range.toMinutes();
        if (minutes <= 60) return Duration.ofMinutes(5); // <= 1h
        if (minutes <= 360) return Duration.ofMinutes(15); // <= 6h
        if (minutes <= 1440) return Duration.ofHours(1); // <= 24h
        if (minutes <= 4320) return Duration.ofHours(3); // <= 3d
        if (minutes <= 10080) return Duration.ofHours(6); // <= 7d
        if (minutes <= 20160) return Duration.ofHours(12); // <= 14d
        if (minutes <= 44640) return Duration.ofDays(1); // <= ~31d
        if (minutes <= 129600) return Duration.ofDays(3); // <= ~90d
        if (minutes <= 259200) return Duration.ofDays(7); // <= ~180d
        return Duration.ofDays(14); // > 180d (up to ~1y)
    }

    private static long bucketStart(Instant ts, Duration bucket, ZoneId zone) {
        var zdt = ts.atZone(zone);
        if (bucket.toDays() >= 1) {
            var days = bucket.toDays();
            var flooredEpochDay = Math.floorDiv(zdt.toLocalDate().toEpochDay(), days) * days;
            return LocalDate.ofEpochDay(flooredEpochDay)
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli();
        }
        var startOfDay = zdt.toLocalDate().atStartOfDay(zone).toInstant();
        var bucketMs = bucket.toMillis();
        var offsetMs = ts.toEpochMilli() - startOfDay.toEpochMilli();
        var flooredOffset = Math.floorDiv(offsetMs, bucketMs) * bucketMs;
        return startOfDay.toEpochMilli() + flooredOffset;
    }

    private static List<Long> bucketStarts(Instant from, Instant to, Duration bucket, ZoneId zone) {
        var endMs = to.toEpochMilli();
        var stepMs = bucket.toMillis();
        List<Long> starts = new ArrayList<>();
        var current = bucketStart(from, bucket, zone);
        while (current <= endMs && starts.size() < MAX_BUCKETS) {
            starts.add(current);
            // Re-align after stepping so day/week boundaries survive DST shifts.
            var next = bucketStart(Instant.ofEpochMilli(current + stepMs + stepMs / 2), bucket, zone);
            current = next > current ? next : current + stepMs;
        }
        if (starts.isEmpty()) {
            starts.add(bucketStart(from, bucket, zone));
        }
        return starts;
    }

    private static double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private static final class StageAccumulator {
        private final Map<String, double[]> stageSumCount = new HashMap<>();
        private final List<Double> totals = new ArrayList<>();

        void add(String stage, Double seconds) {
            if (seconds == null) {
                return;
            }
            var sumCount = stageSumCount.computeIfAbsent(stage, k -> new double[2]);
            sumCount[0] += seconds;
            sumCount[1] += 1;
        }

        void addTotal(Double seconds) {
            if (seconds != null) {
                totals.add(seconds);
            }
        }

        LeadTimeResponse.Point toPoint(long bucketStart) {
            Map<String, Double> avg = new LinkedHashMap<>();
            for (var stage : STAGES) {
                var sumCount = stageSumCount.get(stage);
                // null (not 0) for stages with no data in this bucket -> the line shows a gap.
                avg.put(stage, sumCount != null && sumCount[1] > 0 ? round3(sumCount[0] / sumCount[1]) : null);
            }
            return new LeadTimeResponse.Point(bucketStart, totals.size(), avg, round3(p95(totals)));
        }

        private static double p95(List<Double> values) {
            if (values.isEmpty()) {
                return 0.0;
            }
            var sorted = values.stream().sorted().toList();
            var index = (int) Math.ceil(0.95 * sorted.size()) - 1;
            index = Math.clamp(index, 0, sorted.size() - 1);
            return sorted.get(index);
        }
    }
}
