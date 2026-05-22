package org.pwr.cloud.lab.bff.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;

import java.time.Instant;

@Entity
@Table(name = "order_status_updates", indexes = {
        @Index(name = "idx_osu_order_id", columnList = "order_id"),
        @Index(name = "idx_osu_timestamp", columnList = "timestamp")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SseEventType eventType;

    @Column(nullable = false)
    private String station;

    @Column(nullable = false)
    private Instant timestamp;
}
