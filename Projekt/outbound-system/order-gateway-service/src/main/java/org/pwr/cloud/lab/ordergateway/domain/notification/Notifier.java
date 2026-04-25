package org.pwr.cloud.lab.ordergateway.domain.notification;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public interface Notifier {
    void notifyOrderUpdate(OrderId orderId, String message);
}
