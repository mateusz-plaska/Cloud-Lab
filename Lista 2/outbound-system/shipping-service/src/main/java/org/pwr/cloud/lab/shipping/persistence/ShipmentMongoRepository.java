package org.pwr.cloud.lab.shipping.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShipmentMongoRepository extends MongoRepository<ShipmentDocument, String> {}
