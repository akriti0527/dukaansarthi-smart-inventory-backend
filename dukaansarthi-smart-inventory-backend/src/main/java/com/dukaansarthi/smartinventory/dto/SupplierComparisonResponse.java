package com.dukaansarthi.smartinventory.dto;
import java.math.BigDecimal;
public record SupplierComparisonResponse(String id, String name, BigDecimal unitPrice,
 Integer deliveryDays, BigDecimal rating, Integer available, boolean recommended) {}
