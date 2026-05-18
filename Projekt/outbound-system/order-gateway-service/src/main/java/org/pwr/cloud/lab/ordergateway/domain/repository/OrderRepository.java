package org.pwr.cloud.lab.ordergateway.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findAll();

    List<Order> findByCustomerId(CustomerId customerId);
}
