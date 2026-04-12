package org.pwr.cloud.lab.shipping.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.common.exception.OrderNotFoundException;
import org.pwr.cloud.lab.shipping.domain.Shipment;
import org.pwr.cloud.lab.shipping.domain.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public Shipment getShipment(OrderId orderId) {
        return shipmentRepository.findByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
