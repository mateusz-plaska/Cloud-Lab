package org.pwr.cloud.lab.bff.api.dto.user;

import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.common.domain.model.id.UserId;

import java.time.Instant;

public record UserDto(UserId id, String username, String email, String role, Instant createdAt) {
    public static UserDto from(User user) {
        return new UserDto(user.id(), user.username(), user.email(), user.role().name(), user.createdAt());
    }
}