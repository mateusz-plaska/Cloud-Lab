package org.pwr.cloud.lab.bff.infrastructure.proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ReservationServiceProxy {

    private final RestClient restClient;

    public ReservationServiceProxy(@Qualifier("reservationServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void addStock(String productId, int quantity) {
        restClient
                .post()
                .uri(u -> u.path("/api/stocks")
                        .queryParam("productId", productId)
                        .queryParam("quantity", quantity)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }
}