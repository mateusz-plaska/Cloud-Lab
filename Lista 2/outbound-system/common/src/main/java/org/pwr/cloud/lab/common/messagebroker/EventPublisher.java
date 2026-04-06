package org.pwr.cloud.lab.common.messagebroker;

import org.pwr.cloud.lab.common.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
