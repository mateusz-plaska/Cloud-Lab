package org.pwr.cloud.lab.packing.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pwr.cloud.lab.packing.domain.BoxSize;

@Entity
@Table(name = "box_types", schema = "packing_schema")
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
