package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderProductInfo(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("price")
        Long price
) {
}
