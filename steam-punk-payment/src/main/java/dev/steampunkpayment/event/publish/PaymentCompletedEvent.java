package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.OrderProductInfo;
import dev.steampunkpayment.domain.Payment;
import java.util.List;

public record PaymentCompletedEvent(
        Payment payment,
        List<OrderProductInfo> orderProductInfos
) {
    public static PaymentCompletedEvent from(Payment payment, List<OrderProductInfo> orderProductInfos) {
        return new PaymentCompletedEvent(payment, orderProductInfos);
    }
}
