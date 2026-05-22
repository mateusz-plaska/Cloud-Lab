package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.order.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Service
@Slf4j
public class OrderServiceProxy {

    private final OrderGatewayClient orderGatewayClient;
    private final JsonMapper jsonMapper;
    private final RestClient orderRestClient;

    public OrderServiceProxy(
            OrderGatewayClient orderGatewayClient,
            JsonMapper jsonMapper,
            @Value("${services.order-gateway.url}") String orderGatewayUrl) {
        this.orderGatewayClient = orderGatewayClient;
        this.jsonMapper = jsonMapper;
        this.orderRestClient = RestClient.create(orderGatewayUrl);
    }

    public String getOrders(String customerId) {
        try {
            return orderGatewayClient.getOrders(customerId);
        } catch (Exception e) {
            log.warn("Order gateway unavailable: {}", e.getMessage());
            throw e;
        }
    }

    public String getOrderReport(String orderId) {
        try {
            return orderGatewayClient.getOrderReport(orderId);
        } catch (Exception e) {
            log.warn("Order gateway unavailable for report [{}]: {}", orderId, e.getMessage());
            throw e;
        }
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

            return orderRestClient
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
