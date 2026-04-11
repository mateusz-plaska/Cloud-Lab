package org.pwr.cloud.lab.ordergateway.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.ProductId;

@Builder
public record OrderItem(ProductId productId, int quantity) {}
