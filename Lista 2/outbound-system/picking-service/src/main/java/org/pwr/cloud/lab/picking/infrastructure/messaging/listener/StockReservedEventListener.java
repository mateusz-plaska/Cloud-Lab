package org.pwr.cloud.lab.picking.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.pwr.cloud.lab.picking.application.service.PickingService;
import org.pwr.cloud.lab.picking.domain.model.PickingItem;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockReservedEventListener {
    private final PickingService pickingService;

    @RabbitListener(queues = "${rabbitmq.picking.tasks.queue.name}")
    public void handleStockReservedEvent(StockReservedEvent stockReservedEvent) {
        pickingService.createPickingTask(
                stockReservedEvent.orderId(), convertToPickingItems(stockReservedEvent.items()));
    }

    private List<PickingItem> convertToPickingItems(List<OutboundOrderCreatedEvent.OrderItem> items) {
        return items.stream()
                .map(item -> PickingItem.builder()
                        .productId(item.productId())
                        .requiredQuantity(item.quantity())
                        .pickedQuantity(0)
                        .build())
                .toList();
    }
}
