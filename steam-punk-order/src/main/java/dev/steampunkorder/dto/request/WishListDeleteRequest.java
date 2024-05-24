package dev.steampunkorder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WishListDeleteRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("wish_list_id")
        Long wishListId
) {
}
