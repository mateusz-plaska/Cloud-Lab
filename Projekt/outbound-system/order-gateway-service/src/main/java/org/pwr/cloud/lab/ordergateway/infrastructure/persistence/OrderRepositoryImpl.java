package org.pwr.cloud.lab.ordergateway.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.CustomerId;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;
import org.pwr.cloud.lab.ordergateway.domain.model.OrderItem;
import org.pwr.cloud.lab.ordergateway.domain.repository.OrderRepository;
import org.pwr.cloud.lab.ordergateway.infrastructure.persistence.entity.OrderEntity;
import org.pwr.cloud.lab.ordergateway.infrastructure.persistence.entity.OrderItemEntity;
import org.pwr.cloud.lab.ordergateway.infrastructure.persistence.jpa.OrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        var savedEntity = orderJpaRepository.save(toEntity(order));
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return orderJpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return orderJpaRepository.findByCustomerId(customerId.value()).stream().map(this::toDomain).toList();
    }

    private Order toDomain(OrderEntity entity) {
        return Order.builder()
                .orderId(OrderId.of(entity.getOrderId()))
                .customerId(CustomerId.of(entity.getCustomerId()))
                .status(entity.getStatus())
                .items(entity.getItems().stream().map(this::toDomain).toList())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderEntity toEntity(Order order) {
        var orderEntity = OrderEntity.builder()
                .orderId(order.orderId().value())
                .customerId(order.customerId().value())
                .status(order.status())
                .metadata(order.metadata())
                .build();

        return orderEntity.toBuilder()
                .items(order.items().stream()
                        .map(item -> toEntity(item, orderEntity))
                        .toList())
                .build();
    }

    private OrderItem toDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .productId(ProductId.of(entity.getProductId()))
                .quantity(entity.getQuantity())
                .build();
    }

    private OrderItemEntity toEntity(OrderItem item, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(item.id())
                .productId(item.productId().value())
                .quantity(item.quantity())
                .order(orderEntity)
                .build();
    }
}
