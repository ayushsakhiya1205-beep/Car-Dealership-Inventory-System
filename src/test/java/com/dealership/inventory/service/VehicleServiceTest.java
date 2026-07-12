package com.dealership.inventory.service;

import com.dealership.inventory.dto.request.VehicleRequest;
import com.dealership.inventory.dto.response.VehicleResponse;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .id("vehicle123")
                .make("Toyota")
                .model("Camry")
                .year(2022)
                .price(25000.0)
                .stock(10)
                .description("Silver Camry")
                .category("Sedan")
                .build();

        vehicleRequest = new VehicleRequest("Toyota", "Camry", 2022, 25000.0, 10, "Silver Camry", "Sedan");
    }

    @Test
    void createVehicle_ShouldReturnVehicleResponse() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.createVehicle(vehicleRequest);

        assertNotNull(response);
        assertEquals("Toyota", response.getMake());
        assertEquals("Camry", response.getModel());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void getVehicleById_ShouldReturnVehicleResponse_WhenExists() {
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));

        VehicleResponse response = vehicleService.getVehicleById("vehicle123");

        assertNotNull(response);
        assertEquals("Camry", response.getModel());
    }

    @Test
    void getVehicleById_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(vehicleRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById("nonexistent"));
    }

    @Test
    void updateVehicle_ShouldReturnUpdatedVehicleResponse() {
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.updateVehicle("vehicle123", vehicleRequest);

        assertNotNull(response);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void restockVehicle_ShouldIncrementStock() {
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.restockVehicle("vehicle123", 5);

        assertNotNull(response);
        assertEquals(15, response.getStock());
    }
}
