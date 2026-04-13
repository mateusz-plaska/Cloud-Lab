package org.pwr.cloud.lab.ordergateway.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record OrderItemDto(
        @NotNull @Valid ProductId productId,
        @NotNull @Min(1) @Max(1000000) Integer quantity) {}
