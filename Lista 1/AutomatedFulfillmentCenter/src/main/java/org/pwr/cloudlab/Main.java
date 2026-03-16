package org.pwr.cloudlab;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloudlab.broker.MessageBroker;
import org.pwr.cloudlab.config.AppConfig;
import org.pwr.cloudlab.model.event.ContainerPickedEvent;
import org.pwr.cloudlab.model.event.OrderCreatedEvent;
import org.pwr.cloudlab.model.event.OrderPackedEvent;
import org.pwr.cloudlab.model.event.ShipmentCreatedEvent;
import org.pwr.cloudlab.service.CarrierIntegrationService;
import org.pwr.cloudlab.service.consumer.ContainerRoutingService;
import org.pwr.cloudlab.service.consumer.CustomerNotificationService;
import org.pwr.cloudlab.service.consumer.OrderProgressService;
import org.pwr.cloudlab.service.consumer.ReservationService;
import org.pwr.cloudlab.service.publisher.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Application has started");
        try {
            var broker = MessageBroker.connect(AppConfig.getRabbitUrl());
            registerConsumers(broker);
            startPublishers(broker);
            log.info("All services are running");
        } catch (Exception e) {
            log.error("Critical error", e);
        }
    }

    private static void registerConsumers(MessageBroker broker) {
        var orderProgressService = new OrderProgressService();
        var containerRoutingService = new ContainerRoutingService();
        broker.subscribe(ContainerPickedEvent.class, orderProgressService::handleContainerPicked);
        broker.subscribe(ContainerPickedEvent.class, containerRoutingService::handleContainerPicked);

        var reservationService = new ReservationService();
        broker.subscribe(OrderCreatedEvent.class, reservationService::handleOrderCreated);

        var carrierService = new CarrierIntegrationService(broker);
        broker.subscribe(OrderPackedEvent.class, carrierService::handleOrderPacked);

        var customerNotificationService = new CustomerNotificationService();
        broker.subscribe(ShipmentCreatedEvent.class, customerNotificationService::handleShipmentCreated);
    }

    private static void startPublishers(MessageBroker broker) {
        var interval = AppConfig.getPickingInterval();

        var automatedPickAmbientZone = new AutomatedPickAmbientZone(broker);
        var automatedPickFrozenZone = new AutomatedPickFrozenZone(broker);
        var manualPickZone = new ManualPickZone(broker);

        var scheduledExecutor = Executors.newScheduledThreadPool(5);

        scheduledExecutor.scheduleWithFixedDelay(automatedPickAmbientZone, interval, interval, TimeUnit.SECONDS);
        scheduledExecutor.scheduleWithFixedDelay(automatedPickFrozenZone, interval, interval, TimeUnit.SECONDS);
        scheduledExecutor.scheduleWithFixedDelay(manualPickZone, interval, interval, TimeUnit.SECONDS);

        var storeService = new StoreService(broker, scheduledExecutor);
        var packingStation = new PackingStation(broker, scheduledExecutor);

        scheduledExecutor.schedule(storeService, 0, TimeUnit.SECONDS);
        scheduledExecutor.schedule(packingStation, 0, TimeUnit.SECONDS);
    }
}