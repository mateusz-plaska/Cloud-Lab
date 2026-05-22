package org.pwr.cloud.lab.bff.api.dto.auth;

public record AuthResponse(String token, String userId, String username, String role) {}
