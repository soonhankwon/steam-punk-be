package dev.steampunkproductstock.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductStockAddRequest(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("stock_quantity")
        Long stockQuantity
) {
}
