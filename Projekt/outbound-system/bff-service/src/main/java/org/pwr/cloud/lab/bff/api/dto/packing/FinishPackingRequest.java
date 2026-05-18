package org.pwr.cloud.lab.bff.api.dto.packing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.pwr.cloud.lab.common.domain.model.BoxSize;

public record FinishPackingRequest(@NotBlank BoxSize boxSize, @NotNull @Positive Double weight) {}