package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickingServiceProxy {

    private final PickingClient pickingClient;

    public String pickItem(String orderId, String productId, int quantity) {
        try {
            return pickingClient.pickItem(orderId, productId, quantity);
        } catch (Exception e) {
            log.warn("Picking service unavailable for order [{}]: {}", orderId, e.getMessage());
            throw e;
        }
    }

    public String failItem(String orderId, String productId, String reason) {
        try {
            return pickingClient.failItem(orderId, productId, reason);
        } catch (Exception e) {
            log.warn("Picking service unavailable for order [{}]: {}", orderId, e.getMessage());
            throw e;
        }
    }
}
