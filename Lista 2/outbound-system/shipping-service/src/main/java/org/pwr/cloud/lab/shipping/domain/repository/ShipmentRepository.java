package org.pwr.cloud.lab.shipping.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;

import java.util.Optional;

public interface ShipmentRepository {
    void save(Shipment shipment);

    Optional<Shipment> findByOrderId(OrderId orderId);
}
