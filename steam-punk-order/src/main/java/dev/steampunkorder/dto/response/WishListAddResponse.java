package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.WishList;

public record WishListAddResponse(
        @JsonProperty("wish_list_id")
        Long wishListId,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_id")
        Long productId
) {
    public static WishListAddResponse from(WishList wishList) {
        return new WishListAddResponse(
                wishList.getId(),
                wishList.getUserId(),
                wishList.getProductId()
        );
    }
}
