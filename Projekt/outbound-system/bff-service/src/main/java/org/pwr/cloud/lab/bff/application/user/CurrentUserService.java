package org.pwr.cloud.lab.bff.application.user;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.domain.model.Role;
import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found: " + username));
    }

    public boolean hasRole(Role role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    public boolean isUser() {
        return hasRole(Role.USER) && !hasRole(Role.OPERATOR) && !hasRole(Role.ADMIN);
    }
}