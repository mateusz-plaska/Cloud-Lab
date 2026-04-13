package org.pwr.cloud.lab.shipping.infrastructure.persistence.mongo;

import org.pwr.cloud.lab.shipping.infrastructure.persistence.document.ShipmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShipmentMongoRepository extends MongoRepository<ShipmentDocument, String> {}
