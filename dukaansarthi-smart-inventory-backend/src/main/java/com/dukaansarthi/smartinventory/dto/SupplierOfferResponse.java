package com.dukaansarthi.smartinventory.dto;

import com.dukaansarthi.smartinventory.entity.SupplierOffer;
import java.math.BigDecimal;

public record SupplierOfferResponse(
    Long id, Long productId, String productName, String supplierCode,
    String supplierName, BigDecimal unitPrice, Integer deliveryDays,
    Integer availableQuantity, BigDecimal rating, boolean active
) {
    public static SupplierOfferResponse from(SupplierOffer offer) {
        return new SupplierOfferResponse(
            offer.getId(), offer.getProduct().getId(), offer.getProduct().getName(),
            offer.getSupplierCode(), offer.getSupplierName(), offer.getUnitPrice(),
            offer.getDeliveryDays(), offer.getAvailableQuantity(), offer.getRating(), offer.isActive()
        );
    }
}
