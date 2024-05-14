package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;

public record OrderGetResponse(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("total_price")
        Long totalPrice
) {
    public static OrderGetResponse of(Order order, Long totalPrice) {
        return new OrderGetResponse(
                order.getUserId(),
                totalPrice
        );
    }
}
