package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductDiscountPolicy;
import dev.steampunkproduct.enumtype.ProductState;

public record ProductAddResponse(
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
        ProductDiscountPolicy productDiscountPolicy
) {
    public static ProductAddResponse from(Product product) {
        return new ProductAddResponse(
                product.getName(),
                product.getPrice(),
                product.getShortDescription(),
                product.getHeaderImage(),
                product.getWebSite(),
                product.getDeveloper(),
                product.getProductState(),
                product.getProductDiscountPolicy()
        );
    }
}
