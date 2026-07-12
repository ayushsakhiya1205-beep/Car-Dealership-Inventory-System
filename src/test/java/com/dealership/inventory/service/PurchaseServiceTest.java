package com.dealership.inventory.service;

import com.dealership.inventory.dto.request.PurchaseRequest;
import com.dealership.inventory.dto.response.PurchaseResponse;
import com.dealership.inventory.exception.InsufficientStockException;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.Role;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.PurchaseRecordRepository;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PurchaseRecordRepository purchaseRecordRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private User user;
    private Vehicle vehicle;
    private PurchaseRequest purchaseRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user123")
                .username("customer")
                .password("password")
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();

        vehicle = Vehicle.builder()
                .id("vehicle123")
                .make("Tesla")
                .model("Model 3")
                .year(2023)
                .price(40000.0)
                .stock(5)
                .build();

        purchaseRequest = new PurchaseRequest("vehicle123", 2);
    }

    @Test
    void purchaseVehicle_ShouldSucceed_WhenStockIsAvailable() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        PurchaseRecord record = PurchaseRecord.builder()
                .id("record123")
                .userId("user123")
                .vehicleId("vehicle123")
                .purchasePrice(40000.0)
                .quantity(2)
                .build();
        when(purchaseRecordRepository.save(any(PurchaseRecord.class))).thenReturn(record);

        PurchaseResponse response = purchaseService.purchaseVehicle(purchaseRequest, "customer");

        assertNotNull(response);
        assertEquals(80000.0, response.getTotalAmount());
        assertEquals(3, vehicle.getStock()); // 5 - 2
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(purchaseRecordRepository, times(1)).save(any(PurchaseRecord.class));
    }

    @Test
    void purchaseVehicle_ShouldThrowInsufficientStockException_WhenStockIsTooLow() {
        purchaseRequest.setQuantity(6); // Available is 5
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));

        assertThrows(InsufficientStockException.class,
                () -> purchaseService.purchaseVehicle(purchaseRequest, "customer"));

        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(purchaseRecordRepository, never()).save(any(PurchaseRecord.class));
    }
}
