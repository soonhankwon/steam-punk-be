package dev.steampunkorder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderProductDeleteRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("order_Id")
        Long orderId
) {
}
