package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.dashboard.DashboardStatsDto;
import org.pwr.cloud.lab.bff.application.user.CurrentUserService;
import org.pwr.cloud.lab.bff.infrastructure.proxy.OrderServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final OrderServiceProxy orderServiceProxy;
    private final CurrentUserService currentUserService;
    private final JsonMapper jsonMapper;

    @GetMapping
    public ResponseEntity<DashboardStatsDto> getStats() {
        try {
            String customerId = currentUserService.isUser()
                    ? currentUserService.getCurrentUser().id().toCustomerId().value()
                    : null;
            String ordersJson = orderServiceProxy.getOrders(customerId);
            List<Map<String, Object>> orders = jsonMapper.readValue(ordersJson, new TypeReference<>() {});

            Map<String, Long> byStatus = orders.stream()
                    .collect(Collectors.groupingBy(
                            o -> String.valueOf(o.getOrDefault("status", "UNKNOWN")), Collectors.counting()));

            return ResponseEntity.ok(new DashboardStatsDto(orders.size(), byStatus));
        } catch (Exception e) {
            log.error("Failed to fetch dashboard stats", e);
            return ResponseEntity.ok(new DashboardStatsDto(0, Map.of()));
        }
    }
}