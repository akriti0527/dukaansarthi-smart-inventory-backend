package com.dukaansarthi.smartinventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="si_purchase_recommendations")
public class PurchaseRecommendation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="product_id", nullable=false) private InventoryProduct product;
    @Column(nullable=false) private String supplierCode;
    @Column(nullable=false) private String supplierName;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal unitPrice;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal totalCost;
    @Column(nullable=false) private String status = "PENDING_APPROVAL";
    @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime approvedAt;
    public PurchaseRecommendation() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public InventoryProduct getProduct(){return product;} public void setProduct(InventoryProduct p){this.product=p;}
    public String getSupplierCode(){return supplierCode;} public void setSupplierCode(String v){this.supplierCode=v;}
    public String getSupplierName(){return supplierName;} public void setSupplierName(String v){this.supplierName=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){this.quantity=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){this.unitPrice=v;}
    public BigDecimal getTotalCost(){return totalCost;} public void setTotalCost(BigDecimal v){this.totalCost=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
    public LocalDateTime getApprovedAt(){return approvedAt;} public void setApprovedAt(LocalDateTime v){this.approvedAt=v;}
}
