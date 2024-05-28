package dev.steampunkproductstock.repository;

import dev.steampunkproductstock.domain.ProductStock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ps from ProductStock ps WHERE :productId = ps.productId")
    Optional<ProductStock> findByProductIdWithLock(Long productId);

    Optional<ProductStock> findByProductId(Long productId);
}
