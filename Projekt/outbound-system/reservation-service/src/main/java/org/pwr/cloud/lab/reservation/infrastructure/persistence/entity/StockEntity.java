package org.pwr.cloud.lab.reservation.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "stocks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockEntity {
    @Id
    private String productId;

    private Integer quantity;
}
