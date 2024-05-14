package dev.steampunkpayment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserGameHistoryAddRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_ids")
        List<Long> productIds
) {
    public static UserGameHistoryAddRequest of(Long userId, List<Long> orderProductIds) {
        return new UserGameHistoryAddRequest(
                userId,
                orderProductIds
        );
    }
}
