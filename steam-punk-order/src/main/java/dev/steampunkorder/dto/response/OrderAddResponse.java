package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;
import java.time.LocalDateTime;

public record OrderAddResponse(
        @JsonProperty("order_state")
        OrderState orderState,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static OrderAddResponse from(Order order) {
        return new OrderAddResponse(
                order.getOrderState(),
                order.getCreatedAt()
        );
    }
}
