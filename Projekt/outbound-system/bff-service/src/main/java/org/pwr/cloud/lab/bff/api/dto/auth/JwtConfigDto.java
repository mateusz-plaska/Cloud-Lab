package org.pwr.cloud.lab.bff.api.dto.auth;

public record JwtConfigDto(long refreshBeforeExpiryMs, long inactivityLimitMs, SsoConfig sso) {
    public record SsoConfig(
            boolean enabled, String authorizationUri, String clientId, String redirectUri, String scopes) {}
}
