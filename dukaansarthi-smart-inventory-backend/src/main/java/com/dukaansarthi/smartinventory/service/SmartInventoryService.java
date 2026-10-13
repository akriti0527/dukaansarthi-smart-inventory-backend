package com.dukaansarthi.smartinventory.service;

import com.dukaansarthi.smartinventory.dto.*;
import com.dukaansarthi.smartinventory.entity.*;
import com.dukaansarthi.smartinventory.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SmartInventoryService {
    private static final int HISTORY_DAYS = 30;
    private static final int FORECAST_DAYS = 7;
    private final InventoryProductRepository products;
    private final SalesRecordRepository sales;
    private final SupplierOfferRepository offers;
    private final PurchaseRecommendationRepository purchases;

    public SmartInventoryService(InventoryProductRepository products, SalesRecordRepository sales,
            SupplierOfferRepository offers, PurchaseRecommendationRepository purchases) {
        this.products = products; this.sales = sales; this.offers = offers; this.purchases = purchases;
    }

    @Transactional(readOnly = true)
    public List<ForecastResponse> getRecommendations() {
        return products.findAll().stream().map(this::forecastFor).toList();
    }

    @Transactional(readOnly = true)
    public ForecastResponse getForecast(Long productId) {
        return forecastFor(getProduct(productId));
    }

    private ForecastResponse forecastFor(InventoryProduct p) {
        LocalDate today = LocalDate.now();
        Long sold = sales.sumQuantitySold(p.getId(), today.minusDays(HISTORY_DAYS - 1), today);
        long totalSold = sold == null ? 0 : sold;
        double dailyAverage = totalSold / (double) HISTORY_DAYS;
        int nextWeek = (int) Math.ceil(dailyAverage * FORECAST_DAYS);
        int reorderPoint = (int) Math.ceil(dailyAverage * safe(p.getLeadTimeDays())) + safe(p.getSafetyStock());
        int targetStock = nextWeek + reorderPoint;
        int suggested = Math.max(0, targetStock - safe(p.getCurrentStock()) - safe(p.getOnOrderQuantity()));
        String status = safe(p.getCurrentStock()) <= reorderPoint ? "Reorder" :
                safe(p.getCurrentStock()) <= reorderPoint + safe(p.getSafetyStock()) ? "Low" : "OK";
        List<ForecastResponse.ForecastPoint> points = new ArrayList<>();
        for (int i = 0; i < FORECAST_DAYS; i++) {
            // A simple baseline forecast; this intentionally does not claim ML/seasonal accuracy.
            points.add(new ForecastResponse.ForecastPoint(today.plusDays(i + 1).toString(), (int)Math.ceil(dailyAverage * (i + 1))));
        }
        return new ForecastResponse(p.getId(), p.getName(), p.getSku(), p.getCategory(), safe(p.getCurrentStock()),
                nextWeek, reorderPoint, suggested, status, p.getSellingPrice(), dailyAverage, points);
    }

    @Transactional(readOnly = true)
    public List<SupplierComparisonResponse> getSuppliers(Long productId) {
        getProduct(productId);
        List<SupplierOffer> list = offers.findByProduct_IdAndActiveTrue(productId).stream()
                .filter(o -> o.getUnitPrice() != null && o.getAvailableQuantity() != null && o.getAvailableQuantity() > 0)
                .sorted(Comparator.comparing(SupplierOffer::getUnitPrice)
                        .thenComparing(SupplierOffer::getDeliveryDays, Comparator.nullsLast(Integer::compareTo)))
                .toList();
        String recommendedCode = list.stream().min(Comparator
                .comparing(SupplierOffer::getUnitPrice)
                .thenComparing(SupplierOffer::getDeliveryDays, Comparator.nullsLast(Integer::compareTo)))
                .map(SupplierOffer::getSupplierCode).orElse(null);
        return list.stream().map(o -> new SupplierComparisonResponse(o.getSupplierCode(), o.getSupplierName(),
                o.getUnitPrice(), o.getDeliveryDays(), o.getRating(), o.getAvailableQuantity(),
                Objects.equals(o.getSupplierCode(), recommendedCode))).toList();
    }

    @Transactional
    public Map<String, Object> submitPurchaseRequest(
            PurchaseApprovalRequest request) {

        InventoryProduct product = getProduct(request.productId());

        List<SupplierOffer> matchingOffers =
                offers.findByProduct_IdAndSupplierCodeAndActiveTrue(
                        request.productId(),
                        request.supplierId()
                );

        if (matchingOffers.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Supplier offer was not found for this product"
            );
        }

        if (matchingOffers.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Duplicate active supplier offers found for this product. "
                            + "Please resolve the duplicate records."
            );
        }

        SupplierOffer offer = matchingOffers.get(0);

        if (request.quantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero"
            );
        }

        if (request.quantity() > offer.getAvailableQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds supplier availability"
            );
        }

        // Keep your existing purchase recommendation creation
        // and save logic below this point.
        Map<String, Object> response = new HashMap<>();

        response.put("message", "Purchase request submitted successfully");
        response.put("productId", request.productId());
        response.put("supplierId", request.supplierId());
        response.put("quantity", request.quantity());
        response.put("status", "PENDING_APPROVAL");

        return response;
    }

    private InventoryProduct getProduct(Long id) {
        return products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }
    private int safe(Integer n) { return n == null ? 0 : Math.max(0, n); }
}
