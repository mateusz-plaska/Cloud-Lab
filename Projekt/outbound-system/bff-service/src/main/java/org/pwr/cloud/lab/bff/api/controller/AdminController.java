package org.pwr.cloud.lab.bff.api.controller;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.api.dto.user.UserDto;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userRepository.findAll().stream().map(UserDto::from).toList();
        return ResponseEntity.ok(users);
    }
}