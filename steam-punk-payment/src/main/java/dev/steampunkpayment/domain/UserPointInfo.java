package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;

public record UserPointInfo(
        Long point
) {
    public void validatePoint(OrderInfo orderInfo) {
        if (!hasEnoughUserPoint(orderInfo)) {
            throw new ApiException(ErrorCode.NOT_ENOUGH_USER_POINT);
        }
    }

    private boolean hasEnoughUserPoint(OrderInfo orderInfo) {
        return this.point >= orderInfo.totalPrice();
    }
}
