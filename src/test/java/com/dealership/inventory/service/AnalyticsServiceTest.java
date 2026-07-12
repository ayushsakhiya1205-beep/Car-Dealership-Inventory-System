package com.dealership.inventory.service;

import com.dealership.inventory.dto.response.AnalyticsResponse;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.PurchaseRecordRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PurchaseRecordRepository purchaseRecordRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private List<Vehicle> vehicles;
    private List<PurchaseRecord> purchases;

    @BeforeEach
    void setUp() {
        Vehicle v1 = Vehicle.builder()
                .id("v1")
                .make("Tesla")
                .model("Model 3")
                .price(40000.0)
                .stock(2) // Low stock (<= 3)
                .category("Electric")
                .build();

        Vehicle v2 = Vehicle.builder()
                .id("v2")
                .make("Toyota")
                .model("Camry")
                .price(25000.0)
                .stock(10)
                .category("Sedan")
                .build();

        Vehicle v3 = Vehicle.builder()
                .id("v3")
                .make("Ford")
                .model("Mustang")
                .price(35000.0)
                .stock(0) // Out of stock
                .category("Sports")
                .build();

        vehicles = Arrays.asList(v1, v2, v3);

        PurchaseRecord p1 = PurchaseRecord.builder()
                .id("p1")
                .purchasePrice(40000.0)
                .quantity(2)
                .purchasedAt(LocalDateTime.now())
                .build();

        PurchaseRecord p2 = PurchaseRecord.builder()
                .id("p2")
                .purchasePrice(25000.0)
                .quantity(3)
                .purchasedAt(LocalDateTime.now())
                .build();

        purchases = Arrays.asList(p1, p2);
    }

    @Test
    void getAnalyticsSummary_ShouldAggregateStatsCorrectly() {
        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(purchaseRecordRepository.findAll()).thenReturn(purchases);

        AnalyticsResponse response = analyticsService.getAnalyticsSummary();

        assertNotNull(response);
        assertEquals(12, response.getTotalVehicles());
        assertEquals(3L, response.getTotalDistinctModels());
        assertEquals(330000.0, response.getTotalInventoryValue());
        assertEquals(5, response.getTotalSoldVehicles());
        assertEquals(1L, response.getLowStockVehicles());
        assertEquals(1L, response.getOutOfStockVehicles());
    }

    @Test
    void getAnalyticsSummary_ShouldHandleEmptyCollections() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());
        when(purchaseRecordRepository.findAll()).thenReturn(Collections.emptyList());

        AnalyticsResponse response = analyticsService.getAnalyticsSummary();

        assertNotNull(response);
        assertEquals(0, response.getTotalVehicles());
        assertEquals(0L, response.getTotalDistinctModels());
        assertEquals(0.0, response.getTotalInventoryValue());
        assertEquals(0, response.getTotalSoldVehicles());
    }
}
