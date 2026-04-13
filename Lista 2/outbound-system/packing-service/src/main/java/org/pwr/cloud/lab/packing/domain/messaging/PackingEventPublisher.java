package org.pwr.cloud.lab.packing.domain.messaging;

import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public interface PackingEventPublisher {
    void publishPackingFinished(OrderId orderId, double weight, BoxType boxType);
}
