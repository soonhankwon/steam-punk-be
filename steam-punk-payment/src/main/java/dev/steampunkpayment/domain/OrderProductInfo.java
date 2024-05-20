package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.enumtype.OrderProductState;

public record OrderProductInfo(
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("price")
        Long price,
        @JsonProperty("order_product_state")
        OrderProductState orderProductState
) {
}
