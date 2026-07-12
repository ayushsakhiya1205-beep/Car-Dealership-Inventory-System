package com.dealership.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {
    private String id;
    private String userId;
    private String vehicleId;
    private Double purchasePrice;
    private Integer quantity;
    private Double totalAmount;
    private LocalDateTime purchasedAt;
}
