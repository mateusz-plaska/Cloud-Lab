package org.pwr.cloudlab.service;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.broker.MessageBroker;
import org.pwr.cloudlab.model.event.OrderPackedEvent;
import org.pwr.cloudlab.model.event.ShipmentCreatedEvent;

@Slf4j
public record CarrierIntegrationService(MessageBroker broker) {
    public void handleOrderPacked(OrderPackedEvent event) {
        log.info("Received {}", event);
        var shipmentEvent = ShipmentCreatedEvent.from(event.orderId(), event.customerId());
        log.info("Publishing {}", shipmentEvent);
        broker.publish(shipmentEvent);
    }
}