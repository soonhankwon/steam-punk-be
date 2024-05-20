package dev.steampunkproduct.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductStockAddRequest(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("stock_quantity")
        Long stockQuantity
) {
    public static ProductStockAddRequest of(Long productId, Long productStockQuantity) {
        return new ProductStockAddRequest(
                productId,
                productStockQuantity
        );
    }
}
