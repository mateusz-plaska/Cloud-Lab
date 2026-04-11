package org.pwr.cloud.lab.common.domain.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record OrderId(@JsonValue @NotBlank String value) {

    public OrderId {
        Assert.notNull(value, "orderId must not be null");
    }

    @JsonCreator
    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public static OrderId newInstance() {
        return OrderId.of(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
