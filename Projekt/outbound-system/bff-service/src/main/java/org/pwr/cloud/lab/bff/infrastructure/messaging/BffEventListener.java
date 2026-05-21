package org.pwr.cloud.lab.bff.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;
import org.pwr.cloud.lab.bff.api.dto.sse.Station;
import org.pwr.cloud.lab.bff.infrastructure.sse.SseEmitterRegistry;
import org.pwr.cloud.lab.common.domain.event.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "${rabbitmq.bff.queue.name}")
public class BffEventListener {

    private final SseEmitterRegistry sseEmitterRegistry;

    @RabbitHandler
    public void handleOrderCreated(OutboundOrderCreatedEvent event) {
        broadcast(event.orderId().value(), SseEventType.ORDER_CREATED, Station.ORDER_GATEWAY);
    }

    @RabbitHandler
    public void handleStockReserved(StockReservedEvent event) {
        broadcast(event.orderId().value(), SseEventType.STOCK_RESERVED, Station.RESERVATION);
    }

    @RabbitHandler
    public void handleAllocationFailed(AllocationFailedEvent event) {
        broadcast(event.orderId().value(), SseEventType.ALLOCATION_FAILED, Station.RESERVATION);
    }

    @RabbitHandler
    public void handleOrderPicked(OrderPickedEvent event) {
        broadcast(event.orderId().value(), SseEventType.ORDER_PICKED, Station.PICKING);
    }

    @RabbitHandler
    public void handlePickingFailed(OrderPickFailedEvent event) {
        broadcast(event.orderId().value(), SseEventType.PICK_FAILED, Station.PICKING);
    }

    @RabbitHandler
    public void handlePackingFinished(PackingFinishedEvent event) {
        broadcast(event.orderId().value(), SseEventType.PACKING_FINISHED, Station.PACKING);
    }

    @RabbitHandler
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        broadcast(event.orderId().value(), SseEventType.SHIPMENT_CREATED, Station.SHIPPING);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object message) {
        log.debug("BFF received unhandled message type: {}", message.getClass().getSimpleName());
    }

    private void broadcast(String orderId, SseEventType eventType, String station) {
        log.info("SSE broadcast [{}] for order [{}] at station [{}]", eventType, orderId, station);
        sseEmitterRegistry.broadcast(orderId, new OrderStatusUpdate(orderId, eventType, station, Instant.now()));
    }
}
