package org.pwr.cloud.lab.packing.domain.model;

import lombok.Builder;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

@Builder(toBuilder = true)
public record PackingTask(OrderId orderId, PackingStatus status, BoxSize boxSize, double weight) {

    public PackingTask finishPacking(BoxSize newBoxSize, double newWeight) {
        if (this.status == PackingStatus.COMPLETED) {
            throw new IllegalStateException("Packing task for order [" + orderId + "] is already completed.");
        }

        return this.toBuilder()
                .status(PackingStatus.COMPLETED)
                .boxSize(newBoxSize)
                .weight(newWeight)
                .build();
    }
}
