package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;

public record OrderUpdateResponse(
        @JsonProperty("order_state")
        OrderState orderState
) {
    public static OrderUpdateResponse from(Order order) {
        return new OrderUpdateResponse(
                order.getOrderState()
        );
    }
}
