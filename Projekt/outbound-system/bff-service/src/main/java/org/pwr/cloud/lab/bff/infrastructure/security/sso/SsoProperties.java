package org.pwr.cloud.lab.bff.infrastructure.security.sso;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso")
public record SsoProperties(
        boolean enabled,
        String issuerUri,
        String authorizationUri,
        String tokenUri,
        String clientId,
        String clientSecret,
        String redirectUri,
        String scopes) {}
