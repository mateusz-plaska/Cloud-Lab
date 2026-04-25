package org.pwr.cloud.lab.packing.infrastructure.persistence.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.model.BoxSize;
import org.pwr.cloud.lab.packing.infrastructure.persistence.entity.BoxTypeEntity;
import org.pwr.cloud.lab.packing.infrastructure.persistence.jpa.BoxTypeJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final BoxTypeJpaRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("No box types in db. Inserting...");
            repository.saveAll(List.of(
                    new BoxTypeEntity(BoxSize.SMALL, 20.0, 15.0, 10.0),
                    new BoxTypeEntity(BoxSize.MEDIUM, 35.0, 25.0, 15.0),
                    new BoxTypeEntity(BoxSize.LARGE, 50.0, 40.0, 20.0),
                    new BoxTypeEntity(BoxSize.EXTRA_LARGE, 80.0, 50.0, 40.0)));
            log.info("Box types inserted.");
        }
    }
}
