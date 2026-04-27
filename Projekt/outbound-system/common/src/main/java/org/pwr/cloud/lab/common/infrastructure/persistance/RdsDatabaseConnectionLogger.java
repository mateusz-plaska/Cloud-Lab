package org.pwr.cloud.lab.common.infrastructure.persistance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import javax.sql.DataSource;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class RdsDatabaseConnectionLogger implements ApplicationRunner {
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[AWS RDS] Verifying connection with database...");

        try (var connection = dataSource.getConnection()) {
            var dbName = connection.getCatalog();
            var dbUrl = connection.getMetaData().getURL();

            log.info("[AWS RDS] Result: SUCCESS!");
            log.info("[AWS RDS] Connected with database: {}", dbName);
            log.info("[AWS RDS] URL: {}", dbUrl);
        } catch (SQLException e) {
            log.error("[AWS RDS] Result: ERROR!");
            log.error("[AWS RDS] Failed to connect. Reason: {}", e.getMessage());
        }
    }
}
