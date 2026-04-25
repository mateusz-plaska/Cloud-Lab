package org.pwr.cloud.lab.reservation.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.exception.InsufficientStockException;

@Builder(toBuilder = true)
public record Stock(ProductId productId, int quantity) {

    public boolean hasEnough(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }

    public Stock reserve(int requestedQuantity) {
        if (!hasEnough(requestedQuantity)) {
            throw new InsufficientStockException("Not enough stock for product: " + productId);
        }
        return this.toBuilder().quantity(this.quantity - requestedQuantity).build();
    }
}
