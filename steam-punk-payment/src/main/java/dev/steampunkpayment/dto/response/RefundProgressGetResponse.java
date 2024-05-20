package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.enumtype.PaymentState;
import java.time.LocalDateTime;

public record RefundProgressGetResponse(
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
    public static RefundProgressGetResponse of(Long refundTotalPrice, Payment payment) {
        return new RefundProgressGetResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getOrderId(),
                refundTotalPrice,
                payment.getModifiedAt(),
                payment.getPaymentState()
        );
    }
}
