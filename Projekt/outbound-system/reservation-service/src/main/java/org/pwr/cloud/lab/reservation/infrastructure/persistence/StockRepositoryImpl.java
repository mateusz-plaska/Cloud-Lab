package org.pwr.cloud.lab.reservation.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.model.Stock;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
import org.pwr.cloud.lab.reservation.infrastructure.persistence.entity.StockEntity;
import org.pwr.cloud.lab.reservation.infrastructure.persistence.jpa.StockJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<Stock> findAll() {
        return stockJpaRepository.findAll().stream().map(this::toDomain).toList();
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
