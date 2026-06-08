package org.pwr.cloud.lab.bff.api.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.dashboard.LeadTimeResponse;
import org.pwr.cloud.lab.bff.api.dto.dashboard.ThroughputResponse;
import org.pwr.cloud.lab.bff.application.dashboard.DashboardMetricsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardMetricsController {

    private static final Duration DEFAULT_RANGE = Duration.ofHours(24);
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Warsaw");

    private final DashboardMetricsService metricsService;

    @RolesAllowed({"OPERATOR", "ADMIN"})
    @GetMapping("/throughput")
    public ResponseEntity<ThroughputResponse> throughput(
            @RequestParam(required = false) Long fromMs,
            @RequestParam(required = false) Long toMs,
            @RequestParam(required = false) Long bucketMs,
            @RequestParam(required = false) String zone) {
        var to = toMs != null ? Instant.ofEpochMilli(toMs) : Instant.now();
        var from = fromMs != null ? Instant.ofEpochMilli(fromMs) : to.minus(DEFAULT_RANGE);
        var bucket = resolveBucket(from, to, bucketMs);
        return ResponseEntity.ok(metricsService.throughput(from, to, zoneOrDefault(zone), bucket));
    }

    @RolesAllowed({"OPERATOR", "ADMIN"})
    @GetMapping("/lead-time")
    public ResponseEntity<LeadTimeResponse> leadTime(
            @RequestParam(required = false) Long fromMs,
            @RequestParam(required = false) Long toMs,
            @RequestParam(required = false) Long bucketMs,
            @RequestParam(required = false) String zone) {
        var to = toMs != null ? Instant.ofEpochMilli(toMs) : Instant.now();
        var from = fromMs != null ? Instant.ofEpochMilli(fromMs) : to.minus(DEFAULT_RANGE);
        var bucket = resolveBucket(from, to, bucketMs);
        return ResponseEntity.ok(metricsService.leadTime(from, to, zoneOrDefault(zone), bucket));
    }

    private Duration resolveBucket(Instant from, Instant to, Long bucketMs) {
        try {
            return metricsService.resolveBucket(from, to, bucketMs);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private static ZoneId zoneOrDefault(String zone) {
        if (zone != null && !zone.isBlank()) {
            try {
                return ZoneId.of(zone);
            } catch (DateTimeException ignored) {
                // fall through to default on an unknown/invalid zone id
            }
        }
        return DEFAULT_ZONE;
    }
}
