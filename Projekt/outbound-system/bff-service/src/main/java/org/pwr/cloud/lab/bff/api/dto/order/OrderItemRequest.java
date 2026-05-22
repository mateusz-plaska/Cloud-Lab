package org.pwr.cloud.lab.bff.api.dto.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public record OrderItemRequest(
        @NotBlank ProductId productId, @Min(1) @Max(1000000) int quantity) {}
