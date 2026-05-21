package org.pwr.cloud.lab.bff.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.auth.AuthResponse;
import org.pwr.cloud.lab.bff.api.dto.auth.JwtConfigDto;
import org.pwr.cloud.lab.bff.api.dto.auth.LoginRequest;
import org.pwr.cloud.lab.bff.api.dto.auth.RegisterRequest;
import org.pwr.cloud.lab.bff.application.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-before-expiry-ms}")
    private long refreshBeforeExpiryMs;

    @Value("${jwt.inactivity-limit-ms}")
    private long inactivityLimitMs;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/config")
    public ResponseEntity<JwtConfigDto> config() {
        return ResponseEntity.ok(new JwtConfigDto(refreshBeforeExpiryMs, inactivityLimitMs));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.refresh(authentication.getName()));
    }
}