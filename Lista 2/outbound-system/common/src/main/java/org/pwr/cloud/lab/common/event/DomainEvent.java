package org.pwr.cloud.lab.common.event;

public interface DomainEvent {
    default String getEventName() {
        return this.getClass().getSimpleName();
    }
}
