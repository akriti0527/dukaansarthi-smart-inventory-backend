package com.dukaansarthi.smartinventory.repository;
import com.dukaansarthi.smartinventory.entity.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {}
