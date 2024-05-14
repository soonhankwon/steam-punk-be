package dev.steampunkpayment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentAddRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_id")
        Long orderId
) {
}
