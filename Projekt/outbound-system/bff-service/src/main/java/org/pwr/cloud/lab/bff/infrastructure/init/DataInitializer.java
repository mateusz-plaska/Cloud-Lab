package org.pwr.cloud.lab.bff.infrastructure.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.pwr.cloud.lab.bff.domain.model.Role;
import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        seedUser("admin", "admin@warehouse.local", "Admin1234!", Role.ADMIN);
        seedUser("operator", "operator@warehouse.local", "Operator1234!", Role.OPERATOR);
        seedUser("user", "user@warehouse.local", "User1234!", Role.USER);
    }

    private void seedUser(String username, String email, String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
            log.info("Seeded default user: {} ({})", username, role);
        }
    }
}
