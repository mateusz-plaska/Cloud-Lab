package org.pwr.cloud.lab.bff.api.dto.stock;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddStockRequest(@NotBlank String productId, @Min(1) @Max(1000) int quantity) {}
