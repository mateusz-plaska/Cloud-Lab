package org.pwr.cloud.lab.picking.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PickingTaskMongoRepository extends MongoRepository<PickingTaskDocument, String> {
    Optional<PickingTaskDocument> findByOrderId(String orderId);
}
