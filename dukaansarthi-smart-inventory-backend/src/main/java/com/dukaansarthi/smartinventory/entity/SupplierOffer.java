package com.dukaansarthi.smartinventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name="si_supplier_offers", indexes=@Index(name="idx_si_offer_product", columnList="product_id"))
public class SupplierOffer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="product_id", nullable=false) private InventoryProduct product;
    @Column(name="supplier_code", nullable=false) private String supplierCode;
    @Column(name="supplier_name", nullable=false) private String supplierName;
    @DecimalMin("0.0") @Column(nullable=false, precision=12, scale=2) private BigDecimal unitPrice;
    @Min(0) @Column(nullable=false) private Integer deliveryDays = 3;
    @Min(0) @Column(nullable=false) private Integer availableQuantity = 0;
    private BigDecimal rating;
    private boolean active = true;
    public SupplierOffer() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public InventoryProduct getProduct(){return product;} public void setProduct(InventoryProduct p){this.product=p;}
    public String getSupplierCode(){return supplierCode;} public void setSupplierCode(String v){this.supplierCode=v;}
    public String getSupplierName(){return supplierName;} public void setSupplierName(String v){this.supplierName=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){this.unitPrice=v;}
    public Integer getDeliveryDays(){return deliveryDays;} public void setDeliveryDays(Integer v){this.deliveryDays=v;}
    public Integer getAvailableQuantity(){return availableQuantity;} public void setAvailableQuantity(Integer v){this.availableQuantity=v;}
    public BigDecimal getRating(){return rating;} public void setRating(BigDecimal v){this.rating=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){this.active=v;}
}
