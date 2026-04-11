package org.pwr.cloud.lab.ordergateway.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.event.*;
import org.pwr.cloud.lab.ordergateway.application.OrderUpdateService;
import org.pwr.cloud.lab.ordergateway.domain.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "${rabbitmq.order.queue.name}")
public class OrderEventListener {

    private final OrderUpdateService orderUpdateService;

    @RabbitHandler
    public void handleStockReserved(StockReservedEvent event) {
        log.info("Stock reserved for order: {}", event.orderId());
        orderUpdateService.updateStatus(event.orderId(), OrderStatus.IN_PROGRESS, "Inventory allocated");
    }

    @RabbitHandler
    public void handleAllocationFailed(AllocationFailedEvent event) {
        log.error("Allocation failed for order: {}. Reason: {}", event.orderId(), event.reason());
        orderUpdateService.updateStatus(event.orderId(), OrderStatus.FAILED, event.reason());
    }

    @RabbitHandler
    public void handleOrderPicked(OrderPickedEvent event) {
        log.info("Order picked for order: {}", event.orderId());
        orderUpdateService.updateStatus(event.orderId(), OrderStatus.COMPLETED, "Order picked");
    }

    @RabbitHandler
    public void handlePickingFailed(OrderPickFailedEvent event) {
        log.error("Picking failed for order: {}. Reason: {}", event.orderId(), event.reason());
        orderUpdateService.updateStatus(event.orderId(), OrderStatus.FAILED, event.reason());
    }

    @RabbitHandler
    public void handlePackingFinished(PackingFinishedEvent event) {
        log.info("Packing finished for order: {}", event.orderId());
        orderUpdateService.updateStatus(event.orderId(), OrderStatus.PACKED, "Packing finished");
    }

    @RabbitHandler
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        log.info("Shipment created for order: {}. Tracking: {}", event.orderId(), event.trackingNumber());
        orderUpdateService.finalizeOrder(event.orderId(), event.trackingNumber());
    }
}
