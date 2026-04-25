package org.pwr.cloud.lab.picking.infrastructure.persistence.document;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class PickingItemDocument {
    private String productId;
    private Integer requiredQuantity;
    private Integer pickedQuantity;
}
