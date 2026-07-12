package com.dealership.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private Integer totalVehicles;
    private Long totalDistinctModels;
    private Double totalInventoryValue;
    private Integer totalSoldVehicles;
    private Long lowStockVehicles;
    private Long outOfStockVehicles;

    private List<ChartData> categoryDistribution;
    private List<ChartData> inventoryByCategory;
    private List<ChartData> monthlySales;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChartData {
        private String name;
        private Double value;
    }
}
