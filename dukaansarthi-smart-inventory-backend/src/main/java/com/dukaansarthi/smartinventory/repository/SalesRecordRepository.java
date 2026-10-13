package com.dukaansarthi.smartinventory.repository;
import com.dukaansarthi.smartinventory.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {
 @Query("select coalesce(sum(s.quantitySold), 0) from SalesRecord s where s.product.id = :productId and s.saleDate >= :fromDate and s.saleDate <= :toDate")
 Long sumQuantitySold(@Param("productId") Long productId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
