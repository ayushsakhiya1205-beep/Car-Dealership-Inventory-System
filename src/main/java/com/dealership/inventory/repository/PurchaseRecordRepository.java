package com.dealership.inventory.repository;

import com.dealership.inventory.model.PurchaseRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRecordRepository extends MongoRepository<PurchaseRecord, String> {
    List<PurchaseRecord> findByUserId(String userId);
    List<PurchaseRecord> findByVehicleId(String vehicleId);
}
