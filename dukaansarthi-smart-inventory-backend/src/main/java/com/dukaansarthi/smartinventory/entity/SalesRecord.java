package com.dukaansarthi.smartinventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "si_sales_records", indexes = @Index(name="idx_si_sales_product_date", columnList="product_id,sale_date"))
public class SalesRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="product_id", nullable=false) private InventoryProduct product;
    @Positive @Column(nullable=false) private Integer quantitySold;
    @Column(name="sale_date", nullable=false) private LocalDate saleDate;
    public SalesRecord() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public InventoryProduct getProduct(){return product;} public void setProduct(InventoryProduct p){this.product=p;}
    public Integer getQuantitySold(){return quantitySold;} public void setQuantitySold(Integer q){this.quantitySold=q;}
    public LocalDate getSaleDate(){return saleDate;} public void setSaleDate(LocalDate d){this.saleDate=d;}
}
