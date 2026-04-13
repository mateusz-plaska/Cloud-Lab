package org.pwr.cloud.lab.picking.domain.messaging;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

public interface PickingEventPublisher {
    void publishPickingCompleted(OrderId orderId);

    void publishPickingFailed(OrderId orderId, ProductId productId, String reason);
}
