package org.pwr.cloud.lab.packing.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.messagebroker.EventPublisher;
import org.pwr.cloud.lab.packing.domain.BoxSize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackingRabbitMqService {
    private final EventPublisher eventPublisher;

    public void sendPackingFinishedEvent(
            OrderId orderId, double weight, BoxSize boxSize, double length, double width, double height) {
        var event = PackingFinishedEvent.builder()
                .orderId(orderId)
                .weight(weight)
                .boxSize(boxSize.name())
                .length(length)
                .width(width)
                .height(height)
                .build();
        eventPublisher.publish(event);
    }
}
