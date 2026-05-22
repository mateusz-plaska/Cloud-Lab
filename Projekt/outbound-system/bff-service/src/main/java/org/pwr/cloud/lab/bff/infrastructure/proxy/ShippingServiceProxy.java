package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceProxy {

    private final ShippingClient shippingClient;

    public String getShipment(String orderId) {
        try {
            return shippingClient.getShipment(orderId);
        } catch (Exception e) {
            log.warn("Shipping service unavailable for order [{}]: {}", orderId, e.getMessage());
            throw e;
        }
    }
}
