package org.pwr.cloud.lab.packing.infrastructure.messaging.config;

import org.pwr.cloud.lab.common.domain.event.OrderPickedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PackingRabbitMqConfig {

    @Value("${rabbitmq.packing.queue.name}")
    private String queueName;

    @Bean
    public Queue packingQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding packingBinding(TopicExchange outboundExchange, Queue packingQueue) {
        return BindingBuilder.bind(packingQueue).to(outboundExchange).with(OrderPickedEvent.class.getSimpleName());
    }
}
