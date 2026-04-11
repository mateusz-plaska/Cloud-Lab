package org.pwr.cloud.lab.ordergateway.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private Integer quantity;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
