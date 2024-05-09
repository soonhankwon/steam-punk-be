package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproduct.domain.Product;

public record ProductGetResponse(
        @JsonProperty("product_id")
        Long productId
) {
    public static ProductGetResponse from(Product product) {
        return new ProductGetResponse(
                product.getId()
        );
    }
}
