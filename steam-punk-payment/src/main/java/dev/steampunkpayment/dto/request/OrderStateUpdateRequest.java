package dev.steampunkpayment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderStateUpdateRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_state")
        String orderState
) {
    public static OrderStateUpdateRequest of(Long userId, String orderState) {
        return new OrderStateUpdateRequest(userId, orderState);
    }
}
