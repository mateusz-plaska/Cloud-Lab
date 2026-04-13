package org.pwr.cloud.lab.common.domain.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record ProductId(@JsonValue @NotBlank String value) {

    public ProductId {
        Assert.notNull(value, "productId must not be null");
    }

    @JsonCreator
    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public static ProductId newInstance() {
        return ProductId.of(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
