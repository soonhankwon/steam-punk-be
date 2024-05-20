package dev.steampunkorder.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.enumtype.OrderProductState;

public record ProductInfo(
        Long price,
        @JsonProperty("product_state")
        OrderProductState productState
) {
}
