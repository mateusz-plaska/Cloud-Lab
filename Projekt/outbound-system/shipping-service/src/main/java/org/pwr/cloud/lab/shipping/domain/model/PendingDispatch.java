package org.pwr.cloud.lab.shipping.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

import java.time.Instant;

@Builder
public record PendingDispatch(OrderId orderId, double weight, BoxType boxType, Instant dispatchAt) {}
