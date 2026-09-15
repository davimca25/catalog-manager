package com.example.batchservice.repository;

import com.example.batchservice.model.OrderBatchStaging;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderBatchRepository extends MongoRepository<OrderBatchStaging, UUID> {
}
