package org.pwr.cloud.lab.common.domain.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record CustomerId(@JsonValue @NotBlank String value) {

    public CustomerId {
        Assert.notNull(value, "customerId must not be null");
    }

    @JsonCreator
    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public static CustomerId newInstance() {
        return CustomerId.of(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
