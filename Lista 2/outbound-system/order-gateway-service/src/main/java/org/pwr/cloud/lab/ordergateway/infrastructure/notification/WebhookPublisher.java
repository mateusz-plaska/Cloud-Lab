package org.pwr.cloud.lab.ordergateway.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.notification.Notifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookPublisher implements Notifier {

    @Override
    public void notifyOrderUpdate(OrderId orderId, String message) {
        log.info(">>> SEND WEBHOOK TO CUSTOMER <<<");
        log.info("Order: {}, Message: {}", orderId, message);
    }
}
