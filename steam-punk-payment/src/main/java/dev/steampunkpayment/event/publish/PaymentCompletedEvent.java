package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.OrderInfo;

public record PaymentCompletedEvent(
        OrderInfo orderInfo
) {
    public static PaymentCompletedEvent from(OrderInfo orderInfo) {
        return new PaymentCompletedEvent(orderInfo);
    }
}
