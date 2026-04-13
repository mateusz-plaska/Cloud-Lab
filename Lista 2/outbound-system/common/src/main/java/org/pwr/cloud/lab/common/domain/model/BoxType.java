package org.pwr.cloud.lab.common.domain.model;

import lombok.Builder;

@Builder
public record BoxType(BoxSize boxSize, double length, double width, double height) {

    public double volume() {
        return length * width * height;
    }
}
