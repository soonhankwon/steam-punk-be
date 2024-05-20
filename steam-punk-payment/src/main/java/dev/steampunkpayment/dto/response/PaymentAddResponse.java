package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.enumtype.PaymentState;

public record PaymentAddResponse(
        @JsonProperty("payment_id")
        Long paymentId,
        @JsonProperty("total_price")
        Long totalPrice,
        @JsonProperty("payment_state")
        PaymentState paymentState
) {
    public static PaymentAddResponse from(Payment payment) {
        return new PaymentAddResponse(
                payment.getId(),
                payment.getTotalPrice(),
                payment.getPaymentState()
        );
    }
}
