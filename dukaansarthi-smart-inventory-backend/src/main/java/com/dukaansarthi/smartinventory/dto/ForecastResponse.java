package com.dukaansarthi.smartinventory.dto;
import java.math.BigDecimal;
import java.util.List;
public record ForecastResponse(Long productId, String name, String sku, String category,
 Integer currentStock, Integer forecastQuantity, Integer reorderLevel,
 Integer suggestedOrderQuantity, String status, BigDecimal price,
 Double averageDailySales, List<ForecastPoint> forecastPoints) {
 public record ForecastPoint(String date, Integer quantity) {}
}
