package org.pwr.cloud.lab.shipping.infrastructure.persistence;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.Shipment;
import org.pwr.cloud.lab.shipping.domain.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepository {
    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.shipping-table}")
    private String tableName;

    private DynamoDbTable<ShipmentTableRow> table;

    @PostConstruct
    public void init() {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ShipmentTableRow.class));
    }

    @Override
    public Shipment save(Shipment shipment) {
        var row = ShipmentTableRow.from(shipment);
        table.putItem(row);
        return shipment;
    }

    @Override
    public Optional<Shipment> findByOrderId(OrderId orderId) {
        var key = Key.builder()
                .partitionValue(ShipmentTableRow.Indexes.PrimaryIndex.pk(orderId))
                .build();
        return Optional.ofNullable(table.getItem(key)).map(ShipmentTableRow::toModel);
    }
}
