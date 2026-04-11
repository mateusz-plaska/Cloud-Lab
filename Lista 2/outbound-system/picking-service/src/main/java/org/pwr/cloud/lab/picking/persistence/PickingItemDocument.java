package org.pwr.cloud.lab.picking.persistence;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class PickingItemDocument {
    private String productId;
    private Integer requiredQuantity;
    private Integer pickedQuantity;
}
