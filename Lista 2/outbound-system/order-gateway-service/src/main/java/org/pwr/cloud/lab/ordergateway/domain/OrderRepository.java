package org.pwr.cloud.lab.ordergateway.domain;

import org.pwr.cloud.lab.common.domain.id.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findAll();
}
