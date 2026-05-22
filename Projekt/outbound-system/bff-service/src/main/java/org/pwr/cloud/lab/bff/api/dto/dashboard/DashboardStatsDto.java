package org.pwr.cloud.lab.bff.api.dto.dashboard;

import java.util.Map;

public record DashboardStatsDto(int totalOrders, Map<String, Long> byStatus) {}
