package dev.steampunkproduct.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproduct.domain.ProductDiscountPolicy;
import dev.steampunkproduct.domain.ProductState;
import java.util.List;

public record ProductAddRequest(
        String name,
        Double price,
        @JsonProperty("short_description")
        String shortDescription,
        @JsonProperty("header_image")
        String headerImage,
        @JsonProperty("web_site")
        String webSite,
        String developer,
        @JsonProperty("product_state")
        ProductState productState,
        @JsonProperty("discount_policy")
        ProductDiscountPolicy productDiscountPolicy,
        @JsonProperty("stock_quantity")
        Long stockQuantity,
        @JsonProperty("categories")
        List<String> categories
) {
}
