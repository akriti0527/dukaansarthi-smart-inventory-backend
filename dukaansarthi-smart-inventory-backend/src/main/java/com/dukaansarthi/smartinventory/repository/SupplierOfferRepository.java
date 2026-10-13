package com.dukaansarthi.smartinventory.repository;
import com.dukaansarthi.smartinventory.entity.SupplierOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface SupplierOfferRepository extends JpaRepository<SupplierOffer, Long> {
 List<SupplierOffer> findByProduct_IdAndActiveTrue(Long productId);
 List<SupplierOffer> findByProduct_IdAndSupplierCodeAndActiveTrue(
         Long productId,
         String supplierCode
 );
}
