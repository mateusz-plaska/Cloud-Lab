package org.pwr.cloud.lab.picking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.event.StockReservedEvent;
import org.pwr.cloud.lab.picking.application.PickingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockReservedEventListener {
    private final PickingService pickingService;

    @RabbitListener(queues = "${rabbitmq.picking.tasks.queue.name}")
    public void handleStockReservedEvent(StockReservedEvent stockReservedEvent) {
        pickingService.createPickingTask(stockReservedEvent.orderId(), stockReservedEvent.items());
    }
}
