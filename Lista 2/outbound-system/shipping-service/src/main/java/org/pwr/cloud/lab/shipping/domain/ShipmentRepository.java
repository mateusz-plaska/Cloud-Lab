package org.pwr.cloud.lab.shipping.domain;

import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.util.Optional;

public interface ShipmentRepository {
    void save(Shipment shipment);

    Optional<Shipment> findByOrderId(OrderId orderId);
}
