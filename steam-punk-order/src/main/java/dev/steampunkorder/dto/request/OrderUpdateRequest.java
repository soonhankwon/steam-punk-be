package dev.steampunkorder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.enumtype.OrderState;

public record OrderUpdateRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_state")
        OrderState orderState
) {
}
