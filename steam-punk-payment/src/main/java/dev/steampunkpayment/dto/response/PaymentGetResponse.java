package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.PaymentState;
import java.time.LocalDateTime;

public record PaymentGetResponse(
        @JsonProperty("payment_id")
        Long paymentId,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_id")
        Long orderId,
        @JsonProperty("paid_total_price")
        Long paidTotalPrice,
        @JsonProperty("payment_state")
        PaymentState paymentState,
        @JsonProperty("paid_at")
        LocalDateTime paidAt
) {
    public static PaymentGetResponse from(Payment payment) {
        return new PaymentGetResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getOrderId(),
                payment.getTotalPrice(),
                payment.getPaymentState(),
                payment.getModifiedAt()
        );
    }
}
