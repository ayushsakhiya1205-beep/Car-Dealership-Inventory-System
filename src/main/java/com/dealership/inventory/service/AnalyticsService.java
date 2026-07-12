package com.dealership.inventory.service;

import com.dealership.inventory.dto.response.AnalyticsResponse;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.PurchaseRecordRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for aggregating dealership inventory metrics,
 * low stock levels, and historical order details to drive visual chart data.
 */
@Service
public class AnalyticsService {

    private final VehicleRepository vehicleRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;

    @Autowired
    public AnalyticsService(VehicleRepository vehicleRepository,
                            PurchaseRecordRepository purchaseRecordRepository) {
        this.vehicleRepository = vehicleRepository;
        this.purchaseRecordRepository = purchaseRecordRepository;
    }

    /**
     * Aggregates and returns a consolidated summary containing overall KPIs
     * and formatted charting series for categories and monthly sales records.
     *
     * @return populated {@link AnalyticsResponse}
     */
    public AnalyticsResponse getAnalyticsSummary() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<PurchaseRecord> purchases = purchaseRecordRepository.findAll();

        long totalDistinctModels = vehicles.size();
        int totalVehicles = calculateTotalVehicles(vehicles);
        double totalInventoryValue = calculateTotalInventoryValue(vehicles);
        int totalSoldVehicles = calculateTotalSoldVehicles(purchases);
        long lowStockVehicles = countLowStockVehicles(vehicles);
        long outOfStockVehicles = countOutOfStockVehicles(vehicles);

        List<AnalyticsResponse.ChartData> categoryDistribution = computeCategoryDistribution(vehicles);
        List<AnalyticsResponse.ChartData> inventoryByCategory = computeInventoryByCategory(vehicles);
        List<AnalyticsResponse.ChartData> monthlySales = computeMonthlySalesTrend(purchases);

        return AnalyticsResponse.builder()
                .totalVehicles(totalVehicles)
                .totalDistinctModels(totalDistinctModels)
                .totalInventoryValue(totalInventoryValue)
                .totalSoldVehicles(totalSoldVehicles)
                .lowStockVehicles(lowStockVehicles)
                .outOfStockVehicles(outOfStockVehicles)
                .categoryDistribution(categoryDistribution)
                .inventoryByCategory(inventoryByCategory)
                .monthlySales(monthlySales)
                .build();
    }

    private int calculateTotalVehicles(List<Vehicle> vehicles) {
        return vehicles.stream().mapToInt(Vehicle::getStock).sum();
    }

    private double calculateTotalInventoryValue(List<Vehicle> vehicles) {
        return vehicles.stream().mapToDouble(v -> v.getPrice() * v.getStock()).sum();
    }

    private int calculateTotalSoldVehicles(List<PurchaseRecord> purchases) {
        return purchases.stream().mapToInt(PurchaseRecord::getQuantity).sum();
    }

    private long countLowStockVehicles(List<Vehicle> vehicles) {
        return vehicles.stream().filter(v -> v.getStock() > 0 && v.getStock() <= 3).count();
    }

    private long countOutOfStockVehicles(List<Vehicle> vehicles) {
        return vehicles.stream().filter(v -> v.getStock() == 0).count();
    }

    private List<AnalyticsResponse.ChartData> computeCategoryDistribution(List<Vehicle> vehicles) {
        Map<String, Long> modelsByCategoryMap = vehicles.stream()
                .collect(Collectors.groupingBy(
                        v -> getCategoryOrFallback(v.getCategory()),
                        Collectors.counting()
                ));
        return modelsByCategoryMap.entrySet().stream()
                .map(e -> new AnalyticsResponse.ChartData(e.getKey(), e.getValue().doubleValue()))
                .collect(Collectors.toList());
    }

    private List<AnalyticsResponse.ChartData> computeInventoryByCategory(List<Vehicle> vehicles) {
        Map<String, Double> valByCategoryMap = vehicles.stream()
                .collect(Collectors.groupingBy(
                        v -> getCategoryOrFallback(v.getCategory()),
                        Collectors.summingDouble(v -> v.getPrice() * v.getStock())
                ));
        return valByCategoryMap.entrySet().stream()
                .map(e -> new AnalyticsResponse.ChartData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private String getCategoryOrFallback(String category) {
        return category == null || category.trim().isEmpty() ? "Other" : category;
    }

    private List<AnalyticsResponse.ChartData> computeMonthlySalesTrend(List<PurchaseRecord> purchases) {
        List<AnalyticsResponse.ChartData> monthlySales = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            String monthLabel = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            double totalSales = purchases.stream()
                    .filter(p -> p.getPurchasedAt() != null)
                    .filter(p -> {
                        LocalDateTime dt = p.getPurchasedAt();
                        return dt.getYear() == ym.getYear() && dt.getMonthValue() == ym.getMonthValue();
                    })
                    .mapToDouble(PurchaseRecord::getQuantity)
                    .sum();

            monthlySales.add(new AnalyticsResponse.ChartData(monthLabel, totalSales));
        }
        return monthlySales;
    }
}
