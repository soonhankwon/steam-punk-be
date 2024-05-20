package dev.steampunkproductstock.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproductstock.domain.ProductStock;

public record ProductStockAddResponse(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("stock_quantity")
        Long stockQuantity
) {
    public static ProductStockAddResponse from(ProductStock productStock) {
        return new ProductStockAddResponse(
                productStock.getProductId(),
                productStock.getStockQuantity()
        );
    }
}
