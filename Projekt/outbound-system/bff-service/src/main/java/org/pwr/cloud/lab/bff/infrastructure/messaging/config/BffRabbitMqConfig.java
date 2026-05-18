package org.pwr.cloud.lab.bff.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BffRabbitMqConfig {

    @Value("${rabbitmq.bff.queue.name}")
    private String bffQueueName;

    @Bean
    public Queue bffEventsQueue() {
        return new Queue(bffQueueName, true);
    }

    @Bean
    public Binding bffEventsBinding(Queue bffEventsQueue, TopicExchange outboundExchange) {
        return BindingBuilder.bind(bffEventsQueue).to(outboundExchange).with("#");
    }
}