package org.pwr.cloudlab.broker;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.config.Util;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageBroker {
    private final Channel channel;

    private MessageBroker(Connection connection) throws Exception {
        this.channel = connection.createChannel();
        log.info("Connected successfully with CloudAMQP broker");
    }

    public static MessageBroker connect(String amqpUrl) {
        try {
            log.info("Connecting with URL: {}", amqpUrl.replaceAll(":[^:]+@", ":***@"));
            var connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(amqpUrl);
            var connection = connectionFactory.newConnection();
            return new MessageBroker(connection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to the message broker", e);
        }
    }

    public <T> void publish(T event) {
        try {
            var channelName = event.getClass().getSimpleName();
            channel.exchangeDeclare(channelName, BuiltinExchangeType.FANOUT);
            var jsonMessage = Util.OBJECT_MAPPER.writeValueAsString(event);
            channel.basicPublish(channelName, "", null, jsonMessage.getBytes(StandardCharsets.UTF_8));
            log.info("Published event: {}", event);
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }
    }

    public <T> void subscribe(Class<T> eventClass, java.util.function.Consumer<T> eventHandler) {
        try {
            var channelName = eventClass.getSimpleName();
            channel.exchangeDeclare(channelName, BuiltinExchangeType.FANOUT);
            var queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, channelName, "");

            log.info("Queue [{}] listening on channel [{}]", queueName, channelName);

            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                try {
                    var event = Util.OBJECT_MAPPER.readValue(delivery.getBody(), eventClass);
                    eventHandler.accept(event);
                } catch (Exception e) {
                    log.error("Error deserializing message", e);
                }
            }, consumerTag -> log.warn("Consumer [{}] cancelled", consumerTag));
        } catch (Exception e) {
            log.error("Error subscribing channel", e);
        }
    }
}
