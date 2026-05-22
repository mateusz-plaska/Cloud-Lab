package org.pwr.cloud.lab.shipping.infrastructure.persistence.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.pwr.cloud.lab.common.domain.model.BoxSize;

import java.time.Instant;

@Entity
@Table(name = "pending_dispatches", indexes = {
        @Index(name = "idx_pending_dispatches_dispatch_at", columnList = "dispatch_at")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingDispatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoxSize boxSize;

    @Column(nullable = false)
    private Double boxLength;

    @Column(nullable = false)
    private Double boxWidth;

    @Column(nullable = false)
    private Double boxHeight;

    @Column(nullable = false)
    private Instant dispatchAt;
}
