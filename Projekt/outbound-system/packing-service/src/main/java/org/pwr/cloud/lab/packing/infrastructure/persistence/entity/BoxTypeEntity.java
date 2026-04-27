package org.pwr.cloud.lab.packing.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxSize;

@Entity
@Table(name = "box_types")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoxTypeEntity {
    @Id
    @Enumerated(EnumType.STRING)
    private BoxSize size;

    private double length;

    private double width;

    private double height;
}
