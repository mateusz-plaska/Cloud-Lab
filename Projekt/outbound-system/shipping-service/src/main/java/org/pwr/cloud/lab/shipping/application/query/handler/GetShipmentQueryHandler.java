package org.pwr.cloud.lab.shipping.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.shipping.application.query.GetShipmentQuery;
import org.pwr.cloud.lab.shipping.domain.exception.OrderNotFoundException;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetShipmentQueryHandler implements QueryHandler<GetShipmentQuery, Shipment> {
    private final ShipmentRepository shipmentRepository;

    @Override
    @Transactional(readOnly = true)
    public Shipment handle(GetShipmentQuery query) {
        return shipmentRepository
                .findByOrderId(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
    }
}
