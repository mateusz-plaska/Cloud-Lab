package org.pwr.cloud.lab.picking.persistence;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "picking_tasks")
@Builder(toBuilder = true)
@Getter
public class PickingTaskDocument {
    @Id
    private String id;

    private String orderId;

    private String status;

    private List<PickingItemDocument> items;
}
