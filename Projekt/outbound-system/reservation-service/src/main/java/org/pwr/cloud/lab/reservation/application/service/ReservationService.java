package org.pwr.cloud.lab.reservation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.event.OutboundOrderCreatedEvent;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.reservation.domain.exception.InsufficientStockException;
import org.pwr.cloud.lab.reservation.domain.messaging.ReservationEventPublisher;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
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
    private final ReservationEventPublisher reservationEventPublisher;

    public void reserve(OrderId orderId, List<OutboundOrderCreatedEvent.OrderItem> items) {
        try {
            items.forEach(item -> {
                var stock = stockRepository
                        .findByProductId(item.productId())
                        .orElseThrow(() -> new InsufficientStockException("Missing product: " + item.productId()));

                var updatedStock = stock.reserve(item.quantity());
                stockRepository.save(updatedStock);
            });

            reservationEventPublisher.publishStockReserved(orderId, items);
        } catch (InsufficientStockException e) {
            log.error("Reservation failed for order {}: {}", orderId, e.getMessage());
            reservationEventPublisher.publishAllocationFailed(orderId, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
