package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;

public record UserPointInfo(
        Long point
) {
    public void validatePoint(Long totalPrice) {
        if (!hasEnoughUserPoint(totalPrice)) {
            throw new ApiException(ErrorCode.NOT_ENOUGH_USER_POINT);
        }
    }

    private boolean hasEnoughUserPoint(Long totalPrice) {
        return this.point >= totalPrice;
    }
}
