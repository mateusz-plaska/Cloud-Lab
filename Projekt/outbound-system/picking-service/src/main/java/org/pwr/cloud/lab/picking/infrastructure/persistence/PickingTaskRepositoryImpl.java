package org.pwr.cloud.lab.picking.infrastructure.persistence;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.picking.domain.model.PickingTask;
import org.pwr.cloud.lab.picking.domain.repository.PickingTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PickingTaskRepositoryImpl implements PickingTaskRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.picking-table}")
    private String tableName;

    private DynamoDbTable<PickingTaskTableRow> table;

    @PostConstruct
    public void init() {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(PickingTaskTableRow.class));
    }

    @Override
    public PickingTask save(PickingTask task) {
        var row = PickingTaskTableRow.from(task);
        table.putItem(row);
        return task;
    }

    @Override
    public Optional<PickingTask> findByOrderId(OrderId orderId) {
        var key = Key.builder()
                .partitionValue(PickingTaskTableRow.Indexes.PrimaryIndex.pk(orderId))
                .build();

        return Optional.ofNullable(table.getItem(key)).map(PickingTaskTableRow::toModel);
    }
}
