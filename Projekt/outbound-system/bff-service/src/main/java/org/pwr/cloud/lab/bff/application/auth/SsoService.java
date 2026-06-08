package org.pwr.cloud.lab.bff.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.auth.AuthResponse;
import org.pwr.cloud.lab.bff.api.dto.auth.SsoExchangeRequest;
import org.pwr.cloud.lab.bff.domain.exception.SsoAuthenticationException;
import org.pwr.cloud.lab.bff.domain.model.Role;
import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.pwr.cloud.lab.bff.infrastructure.security.JwtService;
import org.pwr.cloud.lab.bff.infrastructure.security.sso.SsoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@ConditionalOnProperty(prefix = "sso", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SsoService {

    private final SsoProperties properties;
    private final RestClient ssoRestClient;
    private final JwtDecoder ssoJwtDecoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse exchange(SsoExchangeRequest request) {
        var idToken = exchangeCodeForIdToken(request.code(), request.codeVerifier());
        var claims = verify(idToken);

        var email = claims.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new SsoAuthenticationException("id_token has no email claim", null);
        }

        var user = userRepository.findByEmail(email).orElseGet(() -> provision(email, claims));
        return new AuthResponse(
                jwtService.generateToken(user),
                user.id().value(),
                user.username(),
                user.role().name());
    }

    private String exchangeCodeForIdToken(String code, String codeVerifier) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", properties.redirectUri());
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("code_verifier", codeVerifier);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = ssoRestClient
                    .post()
                    .uri(properties.tokenUri())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(Map.class);

            Object idToken = response == null ? null : response.get("id_token");
            if (idToken == null) {
                throw new SsoAuthenticationException("Token response missing id_token", null);
            }
            return idToken.toString();
        } catch (RestClientException e) {
            throw new SsoAuthenticationException("Token exchange with IdP failed", e);
        }
    }

    private Jwt verify(String idToken) {
        try {
            return ssoJwtDecoder.decode(idToken);
        } catch (JwtException e) {
            throw new SsoAuthenticationException("id_token validation failed", e);
        }
    }

    private User provision(String email, Jwt claims) {
        var username = uniqueUsername(claims, email);
        var user = User.builder()
                .username(username)
                .email(email)
                // SSO users authenticate via the IdP only; store an unusable random hash
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(Role.USER)
                .build();
        log.info("Provisioning new SSO user: {} ({})", username, email);
        return userRepository.save(user);
    }

    private String uniqueUsername(Jwt claims, String email) {
        var preferred = claims.getClaimAsString("name");
        var base =
                (preferred != null && !preferred.isBlank()) ? preferred.trim() : email.substring(0, email.indexOf('@'));
        var candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}
