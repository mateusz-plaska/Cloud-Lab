package org.pwr.cloud.lab.shipping.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.pwr.cloud.lab.shipping.infrastructure.persistence.document.ShipmentDocument;
import org.pwr.cloud.lab.shipping.infrastructure.persistence.mongo.ShipmentMongoRepository;
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
                .shippingCost(shipment.shippingCost())
                .shippedAt(shipment.shippedAt())
                .build();
    }

    private Shipment toDomain(ShipmentDocument shipmentDocument) {
        return Shipment.builder()
                .orderId(OrderId.of(shipmentDocument.getOrderId()))
                .trackingNumber(TrackingNumber.of(shipmentDocument.getTrackingNumber()))
                .shippingCost(shipmentDocument.getShippingCost())
                .shippedAt(shipmentDocument.getShippedAt())
                .build();
    }
}
