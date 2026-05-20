package org.pwr.cloud.lab.bff.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.auth.RegisterRequest;
import org.pwr.cloud.lab.bff.api.dto.user.UserDto;
import org.pwr.cloud.lab.bff.application.auth.AuthService;
import org.pwr.cloud.lab.bff.domain.exception.UserNotFoundException;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userRepository.findAll().stream().map(UserDto::from).toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException(request.username()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.from(user));
    }
}