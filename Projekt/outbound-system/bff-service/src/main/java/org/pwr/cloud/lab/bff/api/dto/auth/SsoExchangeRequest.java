package org.pwr.cloud.lab.bff.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SsoExchangeRequest(
        @NotBlank String code, @NotBlank String codeVerifier) {}
