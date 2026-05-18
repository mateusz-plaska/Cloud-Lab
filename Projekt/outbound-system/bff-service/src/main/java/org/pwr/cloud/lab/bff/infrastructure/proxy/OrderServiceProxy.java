package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.order.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceProxy {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public OrderServiceProxy(
            @Qualifier("orderServiceRestClient") RestClient restClient, JsonMapper jsonMapper) {
        this.restClient = restClient;
        this.jsonMapper = jsonMapper;
    }

    public String getOrders(String customerId) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/api/orders");
                    if (customerId != null) {
                        builder = builder.queryParam("customerId", customerId);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(String.class);
    }

    public String getOrderReport(String orderId) {
        return restClient.get().uri("/api/orders/reports/{id}", orderId).retrieve().body(String.class);
    }

    public String createOrder(CreateOrderRequest request) {
        try {
            var dataMap = Map.of(
                    "customerId", request.userId().toCustomerId(),
                    "items", request.items().stream()
                            .map(item -> Map.of("productId", item.productId(), "quantity", item.quantity()))
                            .toList());
            byte[] dataBytes = jsonMapper.writeValueAsBytes(dataMap);
            byte[] dummyFile = "{\"source\":\"web\"}".getBytes();

            var body = new LinkedMultiValueMap<String, Object>();

            var dataHeaders = new HttpHeaders();
            dataHeaders.setContentType(MediaType.APPLICATION_JSON);
            body.add("data", new org.springframework.http.HttpEntity<>(dataBytes, dataHeaders));

            body.add("file", new ByteArrayResource(dummyFile) {
                @Override
                public String getFilename() {
                    return "order-metadata.json";
                }
            });

            return restClient
                    .post()
                    .uri("/api/orders")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("Failed to create order via proxy", e);
            throw new RuntimeException("Failed to forward order creation: " + e.getMessage(), e);
        }
    }
}