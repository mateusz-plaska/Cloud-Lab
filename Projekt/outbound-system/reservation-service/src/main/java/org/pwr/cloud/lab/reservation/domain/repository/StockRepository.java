package org.pwr.cloud.lab.reservation.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    void save(Stock stock);

    Optional<Stock> findByProductId(ProductId productId);

    List<Stock> findAll();
}
