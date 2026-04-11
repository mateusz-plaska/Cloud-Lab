package org.pwr.cloud.lab.common.domain.event;

public interface DomainEvent {
    default String getEventName() {
        return this.getClass().getSimpleName();
    }
}
