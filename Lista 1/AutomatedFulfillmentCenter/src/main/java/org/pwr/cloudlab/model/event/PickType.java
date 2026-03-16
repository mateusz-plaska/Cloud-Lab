package org.pwr.cloudlab.model.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PickType {
    MANUAL("MANUAL"),
    AUTOMATED_FROZEN("AUTOMATED_FROZEN"),
    AUTOMATED_AMBIENT("AUTOMATED_AMBIENT");

    private final String description;
}
