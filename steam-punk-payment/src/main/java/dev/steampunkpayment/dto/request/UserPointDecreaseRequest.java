package dev.steampunkpayment.dto.request;

import dev.steampunkpayment.domain.OrderInfo;

public record UserPointDecreaseRequest(
        Long point
) {
    public static UserPointDecreaseRequest from(OrderInfo orderInfo) {
        return new UserPointDecreaseRequest(orderInfo.totalPrice());
    }
}
