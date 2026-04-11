package org.pwr.cloud.lab.reservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.id.OrderId;
import org.pwr.cloud.lab.reservation.domain.InsufficientStockException;
import org.pwr.cloud.lab.reservation.domain.StockRepository;
import org.pwr.cloud.lab.reservation.infrastructure.ReservationRabbitMqService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final StockRepository stockRepository;
    private final ReservationRabbitMqService reservationRabbitMqService;

    public void reserve(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items) {
        try {
            for (var item : items) {
                var stock = stockRepository
                        .findByProductId(item.productId())
                        .orElseThrow(() -> new InsufficientStockException("Missing product: " + item.productId()));

                if (!stock.hasEnough(item.quantity())) {
                    throw new InsufficientStockException("Not enough stock for product: " + item.productId());
                }

                var updatedStock = stock.toBuilder()
                        .quantity(stock.quantity() - item.quantity())
                        .build();
                stockRepository.save(updatedStock);
            }

            reservationRabbitMqService.sendStockReserved(orderId);
        } catch (InsufficientStockException e) {
            log.error("Reservation failed for order {}: {}", orderId, e.getMessage());
            reservationRabbitMqService.sendAllocationFailed(orderId, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
