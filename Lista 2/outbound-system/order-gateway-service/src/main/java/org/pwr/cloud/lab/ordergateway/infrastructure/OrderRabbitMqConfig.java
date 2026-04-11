package org.pwr.cloud.lab.ordergateway.infrastructure;

import org.pwr.cloud.lab.common.domain.event.AllocationFailedEvent;
import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitMqConfig {

    @Value("${rabbitmq.order.queue.name}")
    private String orderQueueName;

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueueName, true);
    }

    @Bean
    public Binding stockReservedBinding(Queue orderQueue, TopicExchange outboundExchange) {
        return BindingBuilder.bind(orderQueue).to(outboundExchange).with(StockReservedEvent.class.getSimpleName());
    }

    @Bean
    public Binding allocationFailedBinding(Queue orderQueue, TopicExchange outboundExchange) {
        return BindingBuilder.bind(orderQueue).to(outboundExchange).with(AllocationFailedEvent.class.getSimpleName());
    }
}
