package org.pwr.cloud.lab.ordergateway.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PLANNED("PLANNED"),
    IN_PROGRESS("IN_PROGRESS"),
    FAILED("FAILED"),
    COMPLETED("COMPLETED"),
    PACKED("PACKED"),
    READY("READY");

    private final String name;
}
