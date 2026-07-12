package com.dealership.inventory.repository;

import com.dealership.inventory.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    
    @Query("{ '$or': [ " +
           "  { 'make': { '$regex': ?0, '$options': 'i' } }, " +
           "  { 'model': { '$regex': ?0, '$options': 'i' } }, " +
           "  { 'description': { '$regex': ?0, '$options': 'i' } } " +
           "] }")
    List<Vehicle> searchByKeyword(String keyword);

    List<Vehicle> findByMakeIgnoreCase(String make);

    List<Vehicle> findByModelIgnoreCase(String model);

    List<Vehicle> findByYear(Integer year);

    List<Vehicle> findByPriceBetween(Double minPrice, Double maxPrice);
}
