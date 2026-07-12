package com.dealership.inventory.service;

import com.dealership.inventory.dto.request.VehicleRequest;
import com.dealership.inventory.dto.response.VehicleResponse;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class orchestrating vehicle catalog data persistence,
 * search parameters filter operations, and restocking updates.
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, MongoTemplate mongoTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Persists a new vehicle record in the inventory.
     *
     * @param request payload containing specifications of the vehicle
     * @return populated {@link VehicleResponse} DTO schema
     */
    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .price(request.getPrice())
                .stock(request.getStock())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    public VehicleResponse getVehicleById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return mapToResponse(vehicle);
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse updateVehicle(String id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setPrice(request.getPrice());
        vehicle.setStock(request.getStock());
        vehicle.setDescription(request.getDescription());
        vehicle.setCategory(request.getCategory());

        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    public void deleteVehicle(String id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleResponse restockVehicle(String id, Integer quantity) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        vehicle.setStock(vehicle.getStock() + quantity);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    public List<VehicleResponse> searchVehicles(String keyword, String make, String model, Integer year, Double minPrice, Double maxPrice) {
        Query query = buildSearchQuery(keyword, make, model, year, minPrice, maxPrice);
        List<Vehicle> vehicles = mongoTemplate.find(query, Vehicle.class);
        return vehicles.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private Query buildSearchQuery(String keyword, String make, String model, Integer year, Double minPrice, Double maxPrice) {
        Query query = new Query();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("make").regex(keyword, "i"),
                    Criteria.where("model").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i")
            ));
        }

        if (make != null && !make.trim().isEmpty()) {
            query.addCriteria(Criteria.where("make").is(make));
        }

        if (model != null && !model.trim().isEmpty()) {
            query.addCriteria(Criteria.where("model").is(model));
        }

        if (year != null) {
            query.addCriteria(Criteria.where("year").is(year));
        }

        if (minPrice != null && maxPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        } else if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        } else if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }

        return query;
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .price(vehicle.getPrice())
                .stock(vehicle.getStock())
                .description(vehicle.getDescription())
                .category(vehicle.getCategory())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
