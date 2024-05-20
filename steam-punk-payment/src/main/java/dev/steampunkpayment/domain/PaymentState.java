package dev.steampunkpayment.domain;

public enum PaymentState {
    PAYMENT_READY,
    PAYMENT_COMPLETED,
    PAYMENT_REFUND_IN_PROGRESS,
    PAYMENT_REFUND_COMPLETED,
    PAYMENT_PARTIAL_REFUND_IN_PROGRESS,
    PAYMENT_PARTIAL_REFUND_COMPLETED
}
