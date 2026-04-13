package org.pwr.cloud.lab.ordergateway.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;

import java.util.List;

public record CreateOrderRequestDto(
        @NotNull @Valid CustomerId customerId, @NotEmpty List<@Valid @NotNull OrderItemDto> items) {}
