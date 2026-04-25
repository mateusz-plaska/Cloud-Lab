package org.pwr.cloud.lab.shipping.application.query;

import org.pwr.cloud.lab.common.application.cqs.Query;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;

public record GetShipmentQuery(OrderId orderId) implements Query<Shipment> {}
