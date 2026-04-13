package org.pwr.cloud.lab.common.domain.messaging;

import org.pwr.cloud.lab.common.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
