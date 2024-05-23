package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;

public record OrderAddResponse(
        @JsonProperty("order_state")
        OrderState orderState
) {
    public static OrderAddResponse from(Order order) {
        return new OrderAddResponse(
                order.getOrderState()
        );
    }
}
