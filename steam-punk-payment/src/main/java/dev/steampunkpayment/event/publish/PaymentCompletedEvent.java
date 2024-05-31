package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.Payment;

public record PaymentCompletedEvent(
        Payment payment
) {
    public static PaymentCompletedEvent from(Payment payment) {
        return new PaymentCompletedEvent(payment);
    }
}
