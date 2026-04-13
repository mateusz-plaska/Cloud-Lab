package org.pwr.cloud.lab.shipping.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public Shipment getShipment(OrderId orderId) {
        return shipmentRepository.findByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
