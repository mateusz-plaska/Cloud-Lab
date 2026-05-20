package org.pwr.cloud.lab.reservation.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.domain.model.Product;
import org.pwr.cloud.lab.reservation.domain.repository.ProductRepository;
import org.pwr.cloud.lab.reservation.infrastructure.persistence.entity.ProductEntity;
import org.pwr.cloud.lab.reservation.infrastructure.persistence.jpa.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return toDomain(productJpaRepository.save(toEntity(product)));
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Product> findById(ProductId productId) {
        return productJpaRepository.findById(productId.value()).map(this::toDomain);
    }

    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.productId().value())
                .name(product.name())
                .build();
    }

    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .productId(ProductId.of(entity.getId()))
                .name(entity.getName())
                .build();
    }
}