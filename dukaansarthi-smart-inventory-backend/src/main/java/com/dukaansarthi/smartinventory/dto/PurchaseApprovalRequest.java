package com.dukaansarthi.smartinventory.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
public record PurchaseApprovalRequest(
 @NotNull Long productId,
 @NotBlank String supplierId,
 String supplierName,
 @NotNull @Positive Integer quantity,
 @NotNull BigDecimal unitPrice,
 String status
) {}
