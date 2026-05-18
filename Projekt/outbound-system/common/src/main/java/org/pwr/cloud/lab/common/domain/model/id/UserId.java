package org.pwr.cloud.lab.common.domain.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record UserId(@JsonValue @NotBlank String value) {

    public UserId {
        Assert.notNull(value, "userId must not be null");
    }

    @JsonCreator
    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId newInstance() {
        return UserId.of(UUID.randomUUID().toString());
    }

    public CustomerId toCustomerId() {
        return CustomerId.of(value);
    }

    @Override
    public String toString() {
        return value;
    }
}