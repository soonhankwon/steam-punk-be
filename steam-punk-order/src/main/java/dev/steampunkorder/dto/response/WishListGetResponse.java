package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WishListGetResponse(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_ids")
        List<Long> productIds
) {
    public static WishListGetResponse of(Long userId, List<Long> productIds) {
        return new WishListGetResponse(
                userId,
                productIds
        );
    }
}
