package org.pwr.cloud.lab.bff.api.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.pwr.cloud.lab.common.domain.model.id.UserId;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank UserId userId, @NotEmpty List<@Valid OrderItemRequest> items) {}
