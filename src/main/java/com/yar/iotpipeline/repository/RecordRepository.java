package com.yar.iotpipeline.repository;

import com.yar.iotpipeline.domain.RecordDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Record repository bean for database communication
 * */
@Profile("kafka-consumer")
@Repository
public interface RecordRepository extends MongoRepository<RecordDocument,String> {
}
