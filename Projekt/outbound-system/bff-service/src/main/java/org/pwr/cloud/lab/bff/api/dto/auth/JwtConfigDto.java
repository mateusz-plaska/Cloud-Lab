package org.pwr.cloud.lab.bff.api.dto.auth;

public record JwtConfigDto(long refreshBeforeExpiryMs, long inactivityLimitMs) {}
