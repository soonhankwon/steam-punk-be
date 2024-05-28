package dev.steampunkproductstock.domain;

import dev.steampunkproductstock.common.entity.BaseTimeEntity;
import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;
import dev.steampunkproductstock.dto.request.ProductStockAddRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_stock", indexes = {
        @Index(name = "ps_product_id_idx", columnList = "product_id")
})
public class ProductStock extends BaseTimeEntity {

    private static final long MIN_STOCK_QUANTITY = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_stock_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "stock_quantity")
    private Long stockQuantity;

    private ProductStock(Long productId, Long stockQuantity) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }

    public static ProductStock from(ProductStockAddRequest request) {
        return new ProductStock(
                request.productId(),
                request.stockQuantity()
        );
    }

    public void decreaseStock() {
        if (this.stockQuantity == MIN_STOCK_QUANTITY) {
            throw new ApiException(ErrorCode.MINIMUM_STOCK_REACHED);
        }
        this.stockQuantity--;
    }
}
