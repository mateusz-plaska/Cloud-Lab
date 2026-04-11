package org.pwr.cloud.lab.ordergateway.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookPublisher {
    public void sendWebhookNotification(OrderId orderId, String message) {
        log.info(">>> WYŚLIJ WEBHOOK DO KLIENTA <<<");
        log.info("Zamówienie: {}, Wiadomość: {}", orderId, message);
    }
}
