package org.pwr.cloud.lab.bff.api.exception;

import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.domain.exception.SsoAuthenticationException;
import org.pwr.cloud.lab.common.domain.exception.DomainConflictException;
import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class BffExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleBadCredentials(Exception e) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    @ExceptionHandler(SsoAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleSsoFailure(SsoAuthenticationException e) {
        log.warn("SSO login failed: {}", e.getMessage());
        return error(HttpStatus.UNAUTHORIZED, "SSO login failed");
    }

    @ExceptionHandler(DomainNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(DomainNotFoundException e) {
        return domainError(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(DomainConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DomainConflictException e) {
        return domainError(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleDownstreamClientError(HttpClientErrorException e) {
        log.warn("Downstream service returned client error: {} {}", e.getStatusCode(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleDownstreamServerError(HttpServerErrorException e) {
        log.error("Downstream service returned server error: {} {}", e.getStatusCode(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(ResourceAccessException e) {
        log.error("Downstream service unavailable: {}", e.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Downstream service temporarily unavailable");
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<Map<String, Object>> handleFeignConnectError(RetryableException e) {
        log.error("Downstream service unreachable: {}", e.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Downstream service temporarily unavailable");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignError(FeignException e) {
        log.error("Downstream service error [{}]: {}", e.status(), e.getMessage());
        HttpStatus status = HttpStatus.resolve(e.status());
        return error(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, "Downstream service error");
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCircuitBreakerOpen(CallNotPermittedException e) {
        log.warn("Circuit breaker open for: {}", e.getCausingCircuitBreakerName());
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Downstream service temporarily unavailable");
    }

    private ResponseEntity<Map<String, Object>> domainError(HttpStatus status, DomainRuntimeException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("message", e.getMessage());
        body.put("httpStatus", status.value());
        body.put("code", e.getCode());
        if (e.getArgs() != null && !e.getArgs().isEmpty()) {
            body.put("args", e.getArgs());
        }
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of("timestamp", Instant.now().toString(), "message", message, "httpStatus", status.value()));
    }
}
