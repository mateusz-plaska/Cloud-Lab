package org.pwr.cloud.lab.reservation.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.reservation.application.command.ReserveItemsCommand;
import org.pwr.cloud.lab.reservation.domain.exception.InsufficientStockException;
import org.pwr.cloud.lab.reservation.domain.messaging.ReservationEventPublisher;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveItemsCommandHandler implements CommandHandler<ReserveItemsCommand, Void> {
    private final StockRepository stockRepository;
    private final ReservationEventPublisher reservationEventPublisher;

    @Override
    @Transactional
    public Void handle(ReserveItemsCommand command) {
        try {
            command.items().forEach(item -> {
                var stock = stockRepository
                        .findByProductId(item.productId())
                        .orElseThrow(() -> new InsufficientStockException("Missing product: " + item.productId()));

                var updatedStock = stock.reserve(item.quantity());
                stockRepository.save(updatedStock);
            });

            reservationEventPublisher.publishStockReserved(command.orderId(), command.items());
        } catch (InsufficientStockException e) {
            log.error("Reservation failed for order {}: {}", command.orderId(), e.getMessage());
            reservationEventPublisher.publishAllocationFailed(command.orderId(), e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return null;
    }
}
