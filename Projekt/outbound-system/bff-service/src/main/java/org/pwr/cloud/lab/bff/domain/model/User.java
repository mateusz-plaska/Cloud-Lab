package org.pwr.cloud.lab.bff.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.UserId;

import java.time.Instant;

@Builder
public record User(UserId id, String username, String email, String password, Role role, Instant createdAt) {}
