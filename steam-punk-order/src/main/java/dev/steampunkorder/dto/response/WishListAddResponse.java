package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.WishList;
import java.time.LocalDateTime;

public record WishListAddResponse(
        @JsonProperty("wish_list_id")
        Long wishListId,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static WishListAddResponse from(WishList wishList) {
        return new WishListAddResponse(
                wishList.getId(),
                wishList.getUserId(),
                wishList.getProductId(),
                wishList.getCreatedAt()
        );
    }
}
