package org.pwr.cloud.lab.reservation.api.dto;

import org.pwr.cloud.lab.reservation.domain.model.Product;

public record ProductDto(String productId, String name) {
    public static ProductDto from(Product product) {
        return new ProductDto(product.productId().value(), product.name());
    }
}