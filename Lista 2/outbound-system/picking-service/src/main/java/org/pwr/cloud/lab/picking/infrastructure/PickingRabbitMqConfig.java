package org.pwr.cloud.lab.picking.infrastructure;

import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PickingRabbitMqConfig {

    @Value("${rabbitmq.picking.tasks.queue.name}")
    private String pickingTaskQueueName;

    @Bean
    public Queue pickingTaskQueue() {
        return new Queue(pickingTaskQueueName, true);
    }

    @Bean
    public Binding pickingTaskBinding(Queue pickingTaskQueue, TopicExchange outboundExchange) {
        return BindingBuilder.bind(pickingTaskQueue)
                .to(outboundExchange)
                .with(StockReservedEvent.class.getSimpleName());
    }
}
