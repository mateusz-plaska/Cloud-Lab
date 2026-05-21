package org.pwr.cloud.lab.bff.api.dto.product;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(@NotBlank String name) {}
