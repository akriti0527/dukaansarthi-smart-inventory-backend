package com.dukaansarthi.smartinventory.controller;

import com.dukaansarthi.smartinventory.dto.SupplierOfferRequest;
import com.dukaansarthi.smartinventory.dto.SupplierOfferResponse;
import com.dukaansarthi.smartinventory.entity.InventoryProduct;
import com.dukaansarthi.smartinventory.entity.SupplierOffer;
import com.dukaansarthi.smartinventory.repository.InventoryProductRepository;
import com.dukaansarthi.smartinventory.repository.SupplierOfferRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CatalogController {
    private final InventoryProductRepository productRepository;
    private final SupplierOfferRepository supplierRepository;

    public CatalogController(InventoryProductRepository productRepository,
                             SupplierOfferRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    // PRODUCT APIs
    @GetMapping("/products")
    public List<InventoryProduct> getProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public InventoryProduct getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryProduct createProduct(@Valid @RequestBody InventoryProduct request) {
        request.setId(null);
        return productRepository.save(request);
    }

    @PutMapping("/products/{id}")
    public InventoryProduct updateProduct(@PathVariable Long id, @Valid @RequestBody InventoryProduct request) {
        InventoryProduct product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setCurrentStock(request.getCurrentStock());
        product.setSafetyStock(request.getSafetyStock());
        product.setLeadTimeDays(request.getLeadTimeDays());
        product.setOnOrderQuantity(request.getOnOrderQuantity());
        product.setSellingPrice(request.getSellingPrice());
        return productRepository.save(product);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        InventoryProduct product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Product cannot be deleted because it is referenced by supplier offers or sales records.");
        }
    }

    // SUPPLIER OFFER APIs: each offer links a supplier and its pricing to a product.
    @GetMapping("/suppliers")
    @Transactional(readOnly = true)
    public List<SupplierOfferResponse> getSupplierOffers() {
        return supplierRepository.findAll().stream().map(SupplierOfferResponse::from).toList();
    }

    @GetMapping("/suppliers/{id}")
    @Transactional(readOnly = true)
    public SupplierOfferResponse getSupplierOffer(@PathVariable Long id) {
        return SupplierOfferResponse.from(findOffer(id));
    }

    @GetMapping("/products/{productId}/supplier-offers")
    @Transactional(readOnly = true)
    public List<SupplierOfferResponse> getOffersForProduct(@PathVariable Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        }
        return supplierRepository.findByProduct_IdAndActiveTrue(productId)
            .stream().map(SupplierOfferResponse::from).toList();
    }

    @PostMapping("/suppliers")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public SupplierOfferResponse createSupplierOffer(@Valid @RequestBody SupplierOfferRequest request) {
        SupplierOffer offer = new SupplierOffer();
        applyRequest(offer, request);
        return SupplierOfferResponse.from(supplierRepository.save(offer));
    }

    @PutMapping("/suppliers/{id}")
    @Transactional
    public SupplierOfferResponse updateSupplierOffer(@PathVariable Long id,
                                                       @Valid @RequestBody SupplierOfferRequest request) {
        SupplierOffer offer = findOffer(id);
        applyRequest(offer, request);
        return SupplierOfferResponse.from(supplierRepository.save(offer));
    }

    @DeleteMapping("/suppliers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplierOffer(@PathVariable Long id) {
        SupplierOffer offer = findOffer(id);
        supplierRepository.delete(offer);
    }

    private SupplierOffer findOffer(Long id) {
        return supplierRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier offer not found: " + id));
    }

    private void applyRequest(SupplierOffer offer, SupplierOfferRequest request) {
        InventoryProduct product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Product not found: " + request.getProductId()));
        offer.setProduct(product);
        offer.setSupplierCode(request.getSupplierCode().trim());
        offer.setSupplierName(request.getSupplierName().trim());
        offer.setUnitPrice(request.getUnitPrice());
        offer.setDeliveryDays(request.getDeliveryDays() == null ? 3 : request.getDeliveryDays());
        offer.setAvailableQuantity(request.getAvailableQuantity() == null ? 0 : request.getAvailableQuantity());
        offer.setRating(request.getRating());
        offer.setActive(request.getActive() == null || request.getActive());
    }
}
