package org.pwr.cloud.lab.bff.infrastructure.proxy;

import feign.form.FormData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.order.CreateOrderRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceProxy {

    private final OrderGatewayClient orderGatewayClient;

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
            var data = new OrderGatewayClient.DataPart(
                    request.userId().toCustomerId().value(),
                    request.items().stream()
                            .map(item -> new OrderGatewayClient.DataPart.Item(
                                    item.productId().value(), item.quantity()))
                            .toList());

            var file = new FormData(
                    MediaType.TEXT_PLAIN_VALUE, "order-metadata.txt", "source=web".getBytes(StandardCharsets.UTF_8));

            return orderGatewayClient.createOrder(data, file);
        } catch (Exception e) {
            log.error("Failed to create order via proxy", e);
            throw new RuntimeException("Failed to forward order creation: " + e.getMessage(), e);
        }
    }
}
