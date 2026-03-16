package org.pwr.cloudlab.model.event;

import org.pwr.cloudlab.config.Util;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record ContainerPickedEvent(String containerId, PickType pickType, List<ItemPicked> pickedItems, Long pickedAt) {

    public static ContainerPickedEvent from(PickType pickType) {
        var containerId = "CNT-" + UUID.randomUUID();
        return new ContainerPickedEvent(
                containerId,
                pickType,
                IntStream.range(0, Util.someInteger(1, 5))
                        .mapToObj(i -> ItemPicked.from())
                        .toList(),
                System.currentTimeMillis());
    }

    record ItemPicked(String itemId, String orderId, Integer pickedQuantity) {
        public static ItemPicked from() {
            var itemId = "ITEM-" + UUID.randomUUID();
            var orderId = "ORD-" + UUID.randomUUID();
            return new ItemPicked(itemId, orderId, Util.someInteger(1, 1000));
        }
    }
}
