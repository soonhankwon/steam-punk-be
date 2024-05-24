package dev.steampunkorder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.domain.WishList;
import java.time.LocalDateTime;

public record WishListDTO(
        @JsonProperty("wish_list_id")
        Long wishListId,
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static WishListDTO from(WishList wishList) {
        return new WishListDTO(
                wishList.getId(),
                wishList.getProductId(),
                wishList.getCreatedAt()
        );
    }
}
