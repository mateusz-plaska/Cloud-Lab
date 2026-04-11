package org.pwr.cloud.lab.reservation.infrastructure;

import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationRabbitMqConfig {

    @Value("${rabbitmq.reservation.queue.name}")
    private String queueName;

    @Bean
    public Queue reservationQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding reservationBinding(TopicExchange outboundExchange, Queue reservationQueue) {
        return BindingBuilder.bind(reservationQueue)
                .to(outboundExchange)
                .with(OutboundOrderCreatedEvent.class.getSimpleName());
    }
}
