package org.pwr.cloud.lab.packing.domain;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.id.OrderId;

@Builder(toBuilder = true)
public record PackingTask(OrderId orderId, PackingStatus status, BoxSize boxSize, double weight) {}
