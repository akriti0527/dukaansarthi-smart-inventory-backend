package com.dukaansarthi.smartinventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
@Table(name = "si_products")
public class InventoryProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false) private String name;
    @Column(unique = true) private String sku;
    private String category;
    @PositiveOrZero @Column(nullable = false) private Integer currentStock = 0;
    @PositiveOrZero @Column(nullable = false) private Integer safetyStock = 0;
    @PositiveOrZero @Column(nullable = false) private Integer leadTimeDays = 3;
    @PositiveOrZero @Column(nullable = false) private Integer onOrderQuantity = 0;
    @PositiveOrZero @Column(nullable = false, precision = 12, scale = 2) private BigDecimal sellingPrice = BigDecimal.ZERO;

    public InventoryProduct() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getSku(){return sku;} public void setSku(String sku){this.sku=sku;}
    public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
    public Integer getCurrentStock(){return currentStock;} public void setCurrentStock(Integer v){this.currentStock=v;}
    public Integer getSafetyStock(){return safetyStock;} public void setSafetyStock(Integer v){this.safetyStock=v;}
    public Integer getLeadTimeDays(){return leadTimeDays;} public void setLeadTimeDays(Integer v){this.leadTimeDays=v;}
    public Integer getOnOrderQuantity(){return onOrderQuantity;} public void setOnOrderQuantity(Integer v){this.onOrderQuantity=v;}
    public BigDecimal getSellingPrice(){return sellingPrice;} public void setSellingPrice(BigDecimal v){this.sellingPrice=v;}
}
