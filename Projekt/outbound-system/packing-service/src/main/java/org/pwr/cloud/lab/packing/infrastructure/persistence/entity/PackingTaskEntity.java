package org.pwr.cloud.lab.packing.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.packing.domain.model.PackingStatus;

@Entity
@Table(name = "packing_tasks")
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PackingTaskEntity {
    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private PackingStatus status;

    @Enumerated(EnumType.STRING)
    private BoxSize boxSize;

    private Double weight;
}
