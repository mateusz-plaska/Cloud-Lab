package org.pwr.cloud.lab.reservation.application;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.Stock;
import org.pwr.cloud.lab.reservation.domain.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public void addStock(ProductId productId, Integer quantity) {
        var stock = stockRepository
                .findByProductId(productId)
                .map(existingStock -> existingStock.toBuilder()
                        .quantity(existingStock.quantity() + quantity)
                        .build())
                .orElse(new Stock(productId, quantity));

        stockRepository.save(stock);
    }
}
