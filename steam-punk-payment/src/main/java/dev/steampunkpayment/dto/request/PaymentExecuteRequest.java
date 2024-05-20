package dev.steampunkpayment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentExecuteRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("payment_id")
        Long paymentId
) {
}
