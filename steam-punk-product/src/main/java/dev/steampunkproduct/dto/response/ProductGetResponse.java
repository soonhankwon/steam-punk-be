package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproduct.domain.Product;

public record ProductGetResponse(
        @JsonProperty("product_id")
        Long productId,
        String name,
        Long price,
        String description,
        @JsonProperty("header_image")
        String headerImage,
        @JsonProperty("web_site")
        String webSite,
        String developer

) {
    public static ProductGetResponse from(Product product) {
        return new ProductGetResponse(
                product.getId(),
                product.getName(),
                Math.round(product.getPrice() * 1300 * product.getProductDiscountPolicy().getSaleRate()),
                product.getShortDescription(),
                product.getHeaderImage(),
                product.getWebSite(),
                product.getDeveloper()
        );
    }
}
