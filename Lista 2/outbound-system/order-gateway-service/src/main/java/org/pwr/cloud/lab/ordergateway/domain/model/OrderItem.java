package org.pwr.cloud.lab.ordergateway.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

@Builder
public record OrderItem(ProductId productId, int quantity) {}
