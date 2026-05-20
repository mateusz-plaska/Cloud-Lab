package org.pwr.cloud.lab.reservation.api.dto;

import org.pwr.cloud.lab.reservation.domain.model.Stock;

public record StockDto(String productId, int quantity) {
    public static StockDto from(Stock stock) {
        return new StockDto(stock.productId().value(), stock.quantity());
    }
}