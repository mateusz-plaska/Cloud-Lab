package org.pwr.cloud.lab.bff.infrastructure.proxy;

import org.pwr.cloud.lab.bff.api.dto.packing.FinishPackingRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PackingServiceProxy {

    private final RestClient restClient;

    public PackingServiceProxy(@Qualifier("packingServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String finishPacking(String orderId, FinishPackingRequest request) {
        return restClient
                .post()
                .uri("/api/packing/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}