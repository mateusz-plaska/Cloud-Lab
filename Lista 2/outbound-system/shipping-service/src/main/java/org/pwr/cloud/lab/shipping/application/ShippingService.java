package org.pwr.cloud.lab.shipping.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.Shipment;
import org.pwr.cloud.lab.shipping.domain.ShipmentRepository;
import org.pwr.cloud.lab.shipping.infrastructure.ShippingRabbitMqService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingService {
    private final ShipmentRepository shipmentRepository;
    private final ShippingRabbitMqService shippingRabbitMqService;

    public void createShipment(OrderId orderId) {
        var shipment = Shipment.builder()
                .orderId(orderId)
                .trackingNumber(TrackingNumber.newInstance())
                .shippedAt(Instant.now())
                .build();

        shipmentRepository.save(shipment);
        shippingRabbitMqService.sendShipmentCreatedEvent(orderId, shipment.trackingNumber(), shipment.shippedAt());
    }
}
