package org.pwr.cloud.lab.ordergateway.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.ordergateway.domain.OrderStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderEntity {
    @Id
    private String orderId;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItemEntity> items;

    @ElementCollection
    @CollectionTable(name = "order_metadata", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
