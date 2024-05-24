package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductDiscountPolicy;
import dev.steampunkproduct.enumtype.ProductState;
import java.util.List;
import java.util.Objects;

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
        String developer,
        @JsonProperty("product_state")
        ProductState productState,
        @JsonProperty("discount_policy")
        ProductDiscountPolicy discountPolicy,
        @JsonProperty("categories")
        List<String> categories

) {
    public static ProductGetResponse of(Product product, List<String> categories) {
        if (Objects.isNull(categories)) {
            categories = List.of();
        }
        return new ProductGetResponse(
                product.getId(),
                product.getName(),
                Math.round(product.getPrice() * 1300 * product.getProductDiscountPolicy().getSaleRate()),
                product.getShortDescription(),
                product.getHeaderImage(),
                product.getWebSite(),
                product.getDeveloper(),
                product.getProductState(),
                product.getProductDiscountPolicy(),
                categories
        );
    }
}
