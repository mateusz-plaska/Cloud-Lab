package org.pwr.cloud.lab.common.domain.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record TrackingNumber(@JsonValue @NotBlank String value) {

    public TrackingNumber {
        Assert.notNull(value, "trackingNumber must not be null");
    }

    @JsonCreator
    public static TrackingNumber of(String value) {
        return new TrackingNumber(value);
    }

    public static TrackingNumber newInstance() {
        return TrackingNumber.of("SHIP-" + UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value;
    }
}
