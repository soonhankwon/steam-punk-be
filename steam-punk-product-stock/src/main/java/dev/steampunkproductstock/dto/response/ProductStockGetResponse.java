package dev.steampunkproductstock.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproductstock.domain.ProductStock;

public record ProductStockGetResponse(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("stock_quantity")
        Long stockQuantity
) {
    public static ProductStockGetResponse from(ProductStock productStock) {
        return new ProductStockGetResponse(
                productStock.getProductId(),
                productStock.getStockQuantity()
        );
    }
}
