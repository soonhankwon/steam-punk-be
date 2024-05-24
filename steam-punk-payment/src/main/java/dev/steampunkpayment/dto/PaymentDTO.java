package dev.steampunkpayment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.enumtype.PaymentState;
import java.time.LocalDateTime;

public record PaymentDTO(
        @JsonProperty("payment_id")
        Long paymentId,
        @JsonProperty("order_id")
        Long orderId,
        @JsonProperty("paid_total_price")
        Long paidTotalPrice,
        @JsonProperty("payment_state")
        PaymentState paymentState,
        @JsonProperty("paid_at")
        LocalDateTime paidAt
) {
    public static PaymentDTO from(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getOrderId(),
                payment.getTotalPrice(),
                payment.getPaymentState(),
                payment.getModifiedAt()
        );
    }
}
