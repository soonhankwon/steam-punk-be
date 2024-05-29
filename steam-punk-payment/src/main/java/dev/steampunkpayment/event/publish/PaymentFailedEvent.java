package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.OrderInfo;
import java.util.Set;

public record PaymentFailedEvent(
        OrderInfo orderInfo,
        Set<Long> tempLimitedProductIdsSet
) {
}
