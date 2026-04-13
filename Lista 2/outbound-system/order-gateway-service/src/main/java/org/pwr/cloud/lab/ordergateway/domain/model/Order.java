package org.pwr.cloud.lab.ordergateway.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.TrackingNumber;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record Order(
        OrderId orderId,
        CustomerId customerId,
        OrderStatus status,
        List<OrderItem> items,
        Map<String, String> metadata,
        Instant createdAt,
        Instant updatedAt) {

    public Order updateStatus(OrderStatus newStatus, String reason) {
        if (this.status.ordinal() >= newStatus.ordinal()) return this;

        var newMetadata = new HashMap<>(this.metadata);
        if (reason != null) {
            newMetadata.put("status_" + newStatus.name() + "_reason", reason);
        }

        return this.toBuilder().status(newStatus).metadata(newMetadata).build();
    }

    public Order finalizeOrder(TrackingNumber trackingNumber) {
        var newMetadata = new HashMap<>(this.metadata);
        newMetadata.put("tracking_number", trackingNumber.value());

        return this.toBuilder().status(OrderStatus.READY).metadata(newMetadata).build();
    }
}
