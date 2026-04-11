package org.pwr.cloud.lab.reservation.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.ProductId;

@Builder(toBuilder = true)
public record Stock(ProductId productId, int quantity) {

    public boolean hasEnough(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }
}
