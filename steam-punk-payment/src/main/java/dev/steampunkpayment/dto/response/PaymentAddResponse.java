package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;

public record PaymentAddResponse(
        @JsonProperty("is_paid")
        boolean isPaid,
        @JsonProperty("total_price")
        Long totalPrice
) {
    public static PaymentAddResponse from(Payment payment) {
        return new PaymentAddResponse(
                true,
                payment.getTotalPrice()
        );
    }
}
