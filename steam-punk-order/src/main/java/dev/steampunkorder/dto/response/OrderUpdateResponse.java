package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;
import java.time.LocalDateTime;

public record OrderUpdateResponse(
        @JsonProperty("order_state")
        OrderState orderState,
        @JsonProperty("updated_at")
        LocalDateTime updated_at
) {
    public static OrderUpdateResponse from(Order order) {
        return new OrderUpdateResponse(
                order.getOrderState(),
                order.getModifiedAt()
        );
    }
}
