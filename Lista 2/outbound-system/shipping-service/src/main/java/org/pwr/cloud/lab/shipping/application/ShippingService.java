package org.pwr.cloud.lab.shipping.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.messaging.ShippingEventPublisher;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingService {
    private final ShipmentRepository shipmentRepository;
    private final ShippingEventPublisher shippingEventPublisher;

    public void createShipment(OrderId orderId, double weight, BoxType boxType) {
        var shippingCost = Shipment.calculateShippingCost(weight, boxType);
        var shipment = Shipment.builder()
                .orderId(orderId)
                .trackingNumber(TrackingNumber.newInstance())
                .shippingCost(shippingCost)
                .shippedAt(Instant.now())
                .build();

        shipmentRepository.save(shipment);
        shippingEventPublisher.publishShipmentCreated(
                shipment.orderId(), shipment.trackingNumber(), shipment.shippedAt());
    }
}
