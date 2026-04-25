package org.pwr.cloud.lab.reservation.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.reservation.application.command.AddStockCommand;
import org.pwr.cloud.lab.reservation.domain.model.Stock;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddStockCommandHandler implements CommandHandler<AddStockCommand, Void> {
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public Void handle(AddStockCommand command) {
        var stock = stockRepository
                .findByProductId(command.productId())
                .map(existingStock -> existingStock.toBuilder()
                        .quantity(existingStock.quantity() + command.quantity())
                        .build())
                .orElse(new Stock(command.productId(), command.quantity()));

        stockRepository.save(stock);
        return null;
    }
}
