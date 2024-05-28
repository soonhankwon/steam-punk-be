package dev.steampunkproduct.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductStockInfo(
        @JsonProperty("stock_quantity")
        Long stockQuantity
) {
    public static ProductStockInfo ofNonLimitedProduct() {
        return new ProductStockInfo(null);
    }
}
