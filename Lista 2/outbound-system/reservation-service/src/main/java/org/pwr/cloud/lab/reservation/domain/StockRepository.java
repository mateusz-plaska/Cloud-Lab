package org.pwr.cloud.lab.reservation.domain;

import org.pwr.cloud.lab.common.domain.id.ProductId;

import java.util.Optional;

public interface StockRepository {
    void save(Stock stock);

    Optional<Stock> findByProductId(ProductId productId);
}
