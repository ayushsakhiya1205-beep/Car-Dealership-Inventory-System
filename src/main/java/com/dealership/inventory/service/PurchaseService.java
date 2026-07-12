package com.dealership.inventory.service;

import com.dealership.inventory.dto.request.PurchaseRequest;
import com.dealership.inventory.dto.response.PurchaseResponse;
import com.dealership.inventory.exception.BadRequestException;
import com.dealership.inventory.exception.InsufficientStockException;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.PurchaseRecordRepository;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class orchestrating customer purchase orders, validation,
 * stock decrements, and order transactions listing.
 */
@Service
public class PurchaseService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;

    @Autowired
    public PurchaseService(UserRepository userRepository,
                           VehicleRepository vehicleRepository,
                           PurchaseRecordRepository purchaseRecordRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.purchaseRecordRepository = purchaseRecordRepository;
    }

    /**
     * Executes a vehicle purchase transaction. Stock limits are verified,
     * decremented atomically, and transaction records compiled.
     *
     * @param request purchase parameters containing vehicle ID and quantity
     * @param username username of the buyer customer Account
     * @return populated {@link PurchaseResponse} DTO schema
     */
    public PurchaseResponse purchaseVehicle(PurchaseRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + request.getVehicleId()));

        if (vehicle.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + vehicle.getStock());
        }

        // Decrement stock
        vehicle.setStock(vehicle.getStock() - request.getQuantity());
        vehicleRepository.save(vehicle);

        // Record purchase
        PurchaseRecord record = PurchaseRecord.builder()
                .userId(user.getId())
                .vehicleId(vehicle.getId())
                .purchasePrice(vehicle.getPrice())
                .quantity(request.getQuantity())
                .build();

        PurchaseRecord savedRecord = purchaseRecordRepository.save(record);

        return mapToResponse(savedRecord);
    }

    public List<PurchaseResponse> getPurchaseHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return purchaseRecordRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PurchaseResponse> getAllPurchases() {
        return purchaseRecordRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PurchaseRecord getPurchaseRecordById(String id) {
        return purchaseRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase record not found with id: " + id));
    }

    private PurchaseResponse mapToResponse(PurchaseRecord record) {
        return PurchaseResponse.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .vehicleId(record.getVehicleId())
                .purchasePrice(record.getPurchasePrice())
                .quantity(record.getQuantity())
                .totalAmount(record.getPurchasePrice() * record.getQuantity())
                .purchasedAt(record.getPurchasedAt())
                .build();
    }
}
