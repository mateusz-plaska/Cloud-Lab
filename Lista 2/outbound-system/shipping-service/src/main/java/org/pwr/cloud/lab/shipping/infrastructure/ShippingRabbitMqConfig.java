package org.pwr.cloud.lab.shipping.infrastructure;

import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShippingRabbitMqConfig {

    @Value("${rabbitmq.shipping.queue.name}")
    private String shippingQueueName;

    @Bean
    public Queue shippingQueue() {
        return new Queue(shippingQueueName, true);
    }

    @Bean
    public Binding shippingBinding(Queue shippingQueue, TopicExchange outboundExchange) {
        return BindingBuilder.bind(shippingQueue).to(outboundExchange).with(PackingFinishedEvent.class.getSimpleName());
    }
}
