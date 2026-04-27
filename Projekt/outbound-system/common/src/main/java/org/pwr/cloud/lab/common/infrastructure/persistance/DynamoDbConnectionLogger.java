package org.pwr.cloud.lab.common.infrastructure.persistance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnClass(DynamoDbClient.class)
public class DynamoDbConnectionLogger implements ApplicationRunner {
    private final DynamoDbClient dynamoDbClient;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[AWS DynamoDB] Verifying connection with database...");

        try {
            var response = dynamoDbClient.listTables();
            log.info("[AWS DynamoDB] Result: SUCCESS!");
            log.info(
                    "[AWS DynamoDB] Connected with database. Available tables: {}",
                    response.tableNames().size());
        } catch (Exception e) {
            log.error("[AWS DynamoDB] Result: ERROR!");
            log.error("[AWS DynamoDB] Failed to connect. Reason: {}", e.getMessage());
        }
    }
}
