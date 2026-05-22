package org.pwr.cloud.lab.packing.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.common.domain.messaging.EventPublisher;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.packing.domain.messaging.PackingEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackingRabbitMqPublisher implements PackingEventPublisher {

    private final EventPublisher eventPublisher;

    @Override
    public void publishPackingFinished(OrderId orderId, double weight, BoxType boxType) {
        var event = PackingFinishedEvent.builder()
                .orderId(orderId)
                .weight(weight)
                .boxType(boxType)
                .build();
        eventPublisher.publish(event);
    }
}
