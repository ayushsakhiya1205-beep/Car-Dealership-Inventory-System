package com.dealership.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRecord {

    @Id
    private String id;

    private String userId;

    private String vehicleId;

    private Double purchasePrice;

    private Integer quantity;

    @CreatedDate
    private LocalDateTime purchasedAt;
}
