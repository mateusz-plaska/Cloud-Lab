package org.pwr.cloud.lab.bff.infrastructure.proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ShippingServiceProxy {

    private final RestClient restClient;

    public ShippingServiceProxy(@Qualifier("shippingServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String getShipment(String orderId) {
        return restClient.get().uri("/api/shipments/{orderId}", orderId).retrieve().body(String.class);
    }
}