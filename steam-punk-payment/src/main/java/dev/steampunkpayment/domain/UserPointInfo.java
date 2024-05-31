package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import org.springframework.web.reactive.function.client.WebClient;

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

    public static UserPointInfo fromUserPointInfoInternalApi(Long userId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/users/point/{userId}", userId)
                .retrieve()
                .bodyToMono(UserPointInfo.class)
                .block();
    }
}
