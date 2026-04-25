package org.pwr.cloud.lab.shipping.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.application.command.CreateShipmentCommand;
import org.pwr.cloud.lab.shipping.domain.messaging.ShippingEventPublisher;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateShipmentCommandHandler implements CommandHandler<CreateShipmentCommand, Void> {
    private final ShipmentRepository shipmentRepository;
    private final ShippingEventPublisher shippingEventPublisher;

    @Override
    @Transactional
    public Void handle(CreateShipmentCommand command) {
        var shippingCost = Shipment.calculateShippingCost(command.weight(), command.boxType());
        var shipment = Shipment.builder()
                .orderId(command.orderId())
                .trackingNumber(TrackingNumber.newInstance())
                .shippingCost(shippingCost)
                .shippedAt(Instant.now())
                .build();

        shipmentRepository.save(shipment);
        shippingEventPublisher.publishShipmentCreated(
                shipment.orderId(), shipment.trackingNumber(), shipment.shippedAt());
        return null;
    }
}
