package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;
import java.util.List;

public record OrderGetResponse(
        @JsonProperty("order_id")
        Long orderId,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("total_price")
        Long totalPrice,
        @JsonProperty("order_state")
        OrderState orderState,
        @JsonProperty("order_product_ids")
        List<Long> orderProductIds
) {
    public static OrderGetResponse of(Order order, Long totalPrice, List<Long> orderProductIds) {
        return new OrderGetResponse(
                order.getId(),
                order.getUserId(),
                totalPrice,
                order.getOrderState(),
                orderProductIds
        );
    }
}
