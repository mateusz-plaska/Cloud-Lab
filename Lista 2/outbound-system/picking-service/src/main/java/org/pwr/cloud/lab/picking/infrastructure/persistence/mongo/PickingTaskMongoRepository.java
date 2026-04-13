package org.pwr.cloud.lab.picking.infrastructure.persistence.mongo;

import org.pwr.cloud.lab.picking.infrastructure.persistence.document.PickingTaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PickingTaskMongoRepository extends MongoRepository<PickingTaskDocument, String> {
    Optional<PickingTaskDocument> findByOrderId(String orderId);
}
