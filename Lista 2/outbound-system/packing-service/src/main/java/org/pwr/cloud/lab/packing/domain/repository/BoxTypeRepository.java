package org.pwr.cloud.lab.packing.domain.repository;

import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.common.domain.model.BoxType;

import java.util.Optional;

public interface BoxTypeRepository {
    Optional<BoxType> findBySize(BoxSize size);
}
