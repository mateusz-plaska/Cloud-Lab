package org.pwr.cloud.lab.bff.infrastructure.security.sso;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(prefix = "sso", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class SsoConfig {

    private final SsoProperties properties;

    @Bean
    public JwtDecoder ssoJwtDecoder() {
        var decoder =
                NimbusJwtDecoder.withIssuerLocation(properties.issuerUri()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(properties.issuerUri()), audienceValidator()));
        return decoder;
    }

    @Bean
    public RestClient ssoRestClient() {
        return RestClient.create();
    }

    private OAuth2TokenValidator<Jwt> audienceValidator() {
        return jwt -> jwt.getAudience().contains(properties.clientId())
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token", "Required audience " + properties.clientId() + " missing", null));
    }
}
