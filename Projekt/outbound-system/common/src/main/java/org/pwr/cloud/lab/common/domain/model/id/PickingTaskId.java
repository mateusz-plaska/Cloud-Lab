package org.pwr.cloud.lab.common.domain.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.UUID;

public record PickingTaskId(@JsonValue @NotBlank String value) {

    public PickingTaskId {
        Assert.notNull(value, "pickingTaskId must not be null");
    }

    @JsonCreator
    public static PickingTaskId of(String value) {
        return new PickingTaskId(value);
    }

    public static PickingTaskId newInstance() {
        return PickingTaskId.of(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
