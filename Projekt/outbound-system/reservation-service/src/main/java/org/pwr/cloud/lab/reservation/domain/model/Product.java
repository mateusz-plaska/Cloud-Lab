package org.pwr.cloud.lab.reservation.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;

@Builder
public record Product(ProductId productId, String name) {}
