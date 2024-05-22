package dev.steampunkproduct.repository;

import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByProduct(Product product);
}
