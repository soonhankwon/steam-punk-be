package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WishListDeleteResponse(
        @JsonProperty("is_deleted")
        Boolean isDeleted
) {
    public static WishListDeleteResponse ofSuccess() {
        return new WishListDeleteResponse(
                Boolean.TRUE
        );
    }
}
