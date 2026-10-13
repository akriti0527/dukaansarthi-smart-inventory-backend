
package com.dukaansarthi.smartinventory.controller;

import com.dukaansarthi.smartinventory.dto.ForecastResponse;
import com.dukaansarthi.smartinventory.dto.PurchaseApprovalRequest;
import com.dukaansarthi.smartinventory.dto.SupplierComparisonResponse;
import com.dukaansarthi.smartinventory.service.SmartInventoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart-inventory")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000"
})
public class SmartInventoryController {

    private final SmartInventoryService service;

    public SmartInventoryController(SmartInventoryService service) {
        this.service = service;
    }

    @GetMapping("/reorder-suggestions")
    public List<ForecastResponse> getRecommendations() {
        return service.getRecommendations();
    }

    @GetMapping("/forecast")
    public ForecastResponse getForecast(
            @RequestParam Long productId) {
        return service.getForecast(productId);
    }

    @GetMapping("/suppliers/{productId}")
    public List<SupplierComparisonResponse> getSuppliers(
            @PathVariable Long productId) {
        return service.getSuppliers(productId);
    }

    @PostMapping("/purchase-recommendations/approve")
    public ResponseEntity<Map<String, Object>> approvePurchase(
            @RequestBody PurchaseApprovalRequest request) {

        Map<String, Object> result =
                service.submitPurchaseRequest(request);

        return ResponseEntity.ok(result);
    }
}
