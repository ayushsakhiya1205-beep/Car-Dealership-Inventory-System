package com.dealership.inventory.service;

import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private User user;
    private Vehicle vehicle;
    private PurchaseRecord record;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user123")
                .username("customer")
                .build();

        vehicle = Vehicle.builder()
                .id("vehicle123")
                .make("Tesla")
                .model("Model 3")
                .year(2023)
                .price(40000.0)
                .stock(5)
                .build();

        record = PurchaseRecord.builder()
                .id("purchase123")
                .userId("user123")
                .vehicleId("vehicle123")
                .purchasePrice(40000.0)
                .quantity(2)
                .purchasedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateInvoicePdf_ShouldReturnNonEmptyPdfBytes_WhenPurchaseRecordIsValid() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.of(vehicle));

        byte[] pdfBytes = invoiceService.generateInvoicePdf(record);

        assertNotNull(pdfBytes);
        // INTENTIONALLY FAILING ASSERTION FOR RED PHASE
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateInvoicePdf_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            invoiceService.generateInvoicePdf(record);
        });
    }

    @Test
    void generateInvoicePdf_ShouldThrowResourceNotFoundException_WhenVehicleDoesNotExist() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById("vehicle123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            invoiceService.generateInvoicePdf(record);
        });
    }
}
