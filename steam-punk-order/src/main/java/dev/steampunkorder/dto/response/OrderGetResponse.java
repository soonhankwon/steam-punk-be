package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.domain.ProductInfo;
import dev.steampunkorder.enumtype.OrderProductState;
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
        @JsonProperty("order_products")
        List<OrderProductDTO> orderProducts
) {
    public record OrderProductDTO(
            @JsonProperty("product_id")
            Long productId,
            @JsonProperty("price")
            Long price,
            @JsonProperty("order_product_state")
            OrderProductState orderProductState
    ) {
        public static OrderProductDTO of(Long productId, ProductInfo productInfo) {
            return new OrderProductDTO(
                    productId,
                    productInfo.price(),
                    productInfo.productState()
            );
        }
    }

    public static OrderGetResponse of(Order order, Long totalPrice, List<OrderProductDTO> orderProductDTOS) {
        return new OrderGetResponse(
                order.getId(),
                order.getUserId(),
                totalPrice,
                order.getOrderState(),
                orderProductDTOS
        );
    }
}
