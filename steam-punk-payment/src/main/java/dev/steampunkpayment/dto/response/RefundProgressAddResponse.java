package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.PaymentState;
import java.time.LocalDateTime;

public record RefundProgressAddResponse(
        @JsonProperty("payment_id")
        Long paymentId,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_id")
        Long orderId,
        @JsonProperty("refund_total_price")
        Long refundTotalPrice,
        @JsonProperty("refund_progress_at")
        LocalDateTime refundProgressAt,
        @JsonProperty("payment_state")
        PaymentState paymentState
) {
    public static RefundProgressAddResponse of(Long refundTotalPrice, Payment payment) {
        return new RefundProgressAddResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getOrderId(),
                refundTotalPrice,
                payment.getModifiedAt(),
                payment.getPaymentState()
        );
    }
}
