package org.pwr.cloud.lab.picking.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.ProductId;

@Builder(toBuilder = true)
public record PickingItem(ProductId productId, int requiredQuantity, int pickedQuantity) {

    public boolean isPicked() {
        return pickedQuantity >= requiredQuantity;
    }
}
