package org.pwr.cloud.lab.bff.application.auth;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.auth.AuthResponse;
import org.pwr.cloud.lab.bff.api.dto.auth.LoginRequest;
import org.pwr.cloud.lab.bff.api.dto.auth.RegisterRequest;
import org.pwr.cloud.lab.bff.domain.exception.EmailAlreadyInUseException;
import org.pwr.cloud.lab.bff.domain.exception.UserNotFoundException;
import org.pwr.cloud.lab.bff.domain.exception.UsernameTakenException;
import org.pwr.cloud.lab.bff.domain.model.Role;
import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.pwr.cloud.lab.bff.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        var user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException(request.username()));
        return new AuthResponse(jwtService.generateToken(user), user.id().value(), user.username(), user.role().name());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameTakenException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.USER)
                .build();
        var saved = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(saved), saved.id().value(), saved.username(), saved.role().name());
    }
}