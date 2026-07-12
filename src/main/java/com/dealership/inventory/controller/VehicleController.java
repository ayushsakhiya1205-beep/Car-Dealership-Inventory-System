package com.dealership.inventory.controller;

import com.dealership.inventory.dto.request.RestockRequest;
import com.dealership.inventory.dto.request.VehicleRequest;
import com.dealership.inventory.dto.response.VehicleResponse;
import com.dealership.inventory.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller managing vehicle resources in the dealership's inventory catalog.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Registers a new vehicle into the catalog. Enforces validation constraints.
     *
     * @param request payload containing specifications of the new vehicle
     * @return 201 Created containing the generated vehicle data
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves the specifications of a single vehicle by identifier.
     *
     * @param id vehicle identifier
     * @return 200 OK containing vehicle specifications
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Queries and filters the vehicle inventory catalog based on criteria.
     * Supports partial matches on keyword across multiple text fields.
     *
     * @param keyword search keyword (make, model, description)
     * @param make exact make filter
     * @param model exact model filter
     * @param year exact year filter
     * @param minPrice minimum price filter bounds
     * @param maxPrice maximum price filter bounds
     * @return 200 OK matching vehicles list
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<VehicleResponse> responses = vehicleService.searchVehicles(keyword, make, model, year, minPrice, maxPrice);
        return ResponseEntity.ok(responses);
    }

    /**
     * Updates specifications for a vehicle in the catalog.
     *
     * @param id vehicle identifier
     * @param request payload containing new specifications
     * @return 200 OK containing updated vehicle data
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a vehicle completely from the catalog.
     *
     * @param id vehicle identifier
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Increments the inventory stock count for a vehicle in the catalog.
     *
     * @param id vehicle identifier
     * @param request restock request containing restock quantity
     * @return 200 OK containing updated vehicle data
     */
    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> restockVehicle(
            @PathVariable String id,
            @Valid @RequestBody RestockRequest request) {
        VehicleResponse response = vehicleService.restockVehicle(id, request.getQuantity());
        return ResponseEntity.ok(response);
    }
}
