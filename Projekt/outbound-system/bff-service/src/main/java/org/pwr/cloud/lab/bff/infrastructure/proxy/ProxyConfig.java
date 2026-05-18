package org.pwr.cloud.lab.bff.infrastructure.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProxyConfig {

    @Bean("orderServiceRestClient")
    public RestClient orderServiceRestClient(@Value("${services.order-gateway.url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }

    @Bean("pickingServiceRestClient")
    public RestClient pickingServiceRestClient(@Value("${services.picking.url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }

    @Bean("packingServiceRestClient")
    public RestClient packingServiceRestClient(@Value("${services.packing.url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }

    @Bean("reservationServiceRestClient")
    public RestClient reservationServiceRestClient(@Value("${services.reservation.url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }

    @Bean("shippingServiceRestClient")
    public RestClient shippingServiceRestClient(@Value("${services.shipping.url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }
}