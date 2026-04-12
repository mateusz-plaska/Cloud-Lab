package org.pwr.cloud.lab.shipping.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.PackingFinishedEvent;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.Shipment;
import org.pwr.cloud.lab.shipping.domain.ShipmentRepository;
import org.pwr.cloud.lab.shipping.infrastructure.ShippingRabbitMqService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingService {
    private final ShipmentRepository shipmentRepository;
    private final ShippingRabbitMqService shippingRabbitMqService;

    public void createShipment(PackingFinishedEvent event) {
        var shipment = Shipment.builder()
                .orderId(event.orderId())
                .trackingNumber(TrackingNumber.newInstance())
                .shippingCost(calculateShippingCost(event))
                .shippedAt(Instant.now())
                .build();

        shipmentRepository.save(shipment);
        shippingRabbitMqService.sendShipmentCreatedEvent(
                shipment.orderId(), shipment.trackingNumber(), shipment.shippedAt());
    }

    private BigDecimal calculateShippingCost(PackingFinishedEvent event) {
        var volume = event.length() * event.width() * event.height();
        var cost = 10.0 + (event.weight() * 2.0) + (volume / 1000.0);
        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }
}
