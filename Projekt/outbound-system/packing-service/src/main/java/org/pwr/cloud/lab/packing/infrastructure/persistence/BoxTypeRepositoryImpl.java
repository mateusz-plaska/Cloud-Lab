package org.pwr.cloud.lab.packing.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.packing.domain.repository.BoxTypeRepository;
import org.pwr.cloud.lab.packing.infrastructure.persistence.entity.BoxTypeEntity;
import org.pwr.cloud.lab.packing.infrastructure.persistence.jpa.BoxTypeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoxTypeRepositoryImpl implements BoxTypeRepository {
    private final BoxTypeJpaRepository boxTypeJpaRepository;

    @Override
    public Optional<BoxType> findBySize(BoxSize size) {
        return boxTypeJpaRepository.findById(size).map(this::toDomain);
    }

    private BoxType toDomain(BoxTypeEntity entity) {
        return BoxType.builder()
                .boxSize(entity.getSize())
                .length(entity.getLength())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .build();
    }
}
