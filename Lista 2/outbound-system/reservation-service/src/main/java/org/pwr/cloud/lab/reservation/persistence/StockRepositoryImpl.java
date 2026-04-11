package org.pwr.cloud.lab.reservation.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.Stock;
import org.pwr.cloud.lab.reservation.domain.StockRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public void save(Stock stock) {
        stockJpaRepository.save(toEntity(stock));
    }

    @Override
    public Optional<Stock> findByProductId(ProductId productId) {
        return stockJpaRepository.findById(productId.value()).map(this::toDomain);
    }

    private StockEntity toEntity(Stock stock) {
        return StockEntity.builder()
                .productId(stock.productId().value())
                .quantity(stock.quantity())
                .build();
    }

    private Stock toDomain(StockEntity entity) {
        return Stock.builder()
                .productId(ProductId.of(entity.getProductId()))
                .quantity(entity.getQuantity())
                .build();
    }
}
