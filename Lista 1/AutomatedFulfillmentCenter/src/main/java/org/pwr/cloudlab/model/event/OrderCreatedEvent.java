package org.pwr.cloudlab.model.event;

import org.pwr.cloudlab.config.Util;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record OrderCreatedEvent(String orderId, List<Item> items, String customerId, Long createdAt) {

    public static OrderCreatedEvent from() {
        var orderId = "ORD-" + UUID.randomUUID();
        var customerId = "CUST-" + UUID.randomUUID();
        return new OrderCreatedEvent(
                orderId,
                IntStream.range(0, Util.someInteger(1, 5))
                        .mapToObj(i -> Item.from())
                        .toList(),
                customerId,
                System.currentTimeMillis());
    }

    record Item(String itemId, Integer quantity) {
        public static Item from() {
            var itemId = "ITEM-" + UUID.randomUUID();
            return new Item(itemId, Util.someInteger(1, 1000));
        }
    }
}
