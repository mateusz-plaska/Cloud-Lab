package org.pwr.cloud.lab.common.messagebroker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.event.DomainEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqEventPublisher implements EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.system.exchange.name}")
    private String exchangeName;

    @Override
    public void publish(DomainEvent event) {
        var routingKey = event.getEventName();
        log.info("Publishing event [{}] to [{}]: {}", routingKey, exchangeName, event);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
}
