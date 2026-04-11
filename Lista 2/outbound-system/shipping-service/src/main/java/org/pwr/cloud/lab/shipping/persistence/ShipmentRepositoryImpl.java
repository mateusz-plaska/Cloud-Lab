package org.pwr.cloud.lab.shipping.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.domain.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.Shipment;
import org.pwr.cloud.lab.shipping.domain.ShipmentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepository {
    private final ShipmentMongoRepository shipmentMongoRepository;

    @Override
    public void save(Shipment shipment) {
        shipmentMongoRepository.save(toDocument(shipment));
    }

    @Override
    public Optional<Shipment> findByOrderId(OrderId orderId) {
        return shipmentMongoRepository.findById(orderId.value()).map(this::toDomain);
    }

    private ShipmentDocument toDocument(Shipment shipment) {
        return ShipmentDocument.builder()
                .orderId(shipment.orderId().value())
                .trackingNumber(shipment.trackingNumber().value())
                .shippedAt(shipment.shippedAt())
                .build();
    }

    private Shipment toDomain(ShipmentDocument shipmentDocument) {
        return Shipment.builder()
                .orderId(OrderId.of(shipmentDocument.getOrderId()))
                .trackingNumber(TrackingNumber.of(shipmentDocument.getTrackingNumber()))
                .shippedAt(shipmentDocument.getShippedAt())
                .build();
    }
}
