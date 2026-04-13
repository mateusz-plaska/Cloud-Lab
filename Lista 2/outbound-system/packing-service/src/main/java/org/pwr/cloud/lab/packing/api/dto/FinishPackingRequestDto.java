package org.pwr.cloud.lab.packing.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.pwr.cloud.lab.common.domain.model.BoxSize;

public record FinishPackingRequestDto(
        @NotNull BoxSize boxSize, @NotNull @Positive Double weight) {}
