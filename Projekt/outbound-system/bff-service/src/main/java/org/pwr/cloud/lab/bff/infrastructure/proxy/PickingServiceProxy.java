package org.pwr.cloud.lab.bff.infrastructure.proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PickingServiceProxy {

    private final RestClient restClient;

    public PickingServiceProxy(@Qualifier("pickingServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String pickItem(String orderId, String productId, int quantity) {
        return restClient
                .post()
                .uri(u -> u.path("/api/picking/{orderId}/pick")
                        .queryParam("productId", productId)
                        .queryParam("quantity", quantity)
                        .build(orderId))
                .retrieve()
                .body(String.class);
    }

    public String failItem(String orderId, String productId, String reason) {
        return restClient
                .post()
                .uri(u -> u.path("/api/picking/{orderId}/fail")
                        .queryParam("productId", productId)
                        .queryParam("reason", reason)
                        .build(orderId))
                .retrieve()
                .body(String.class);
    }
}