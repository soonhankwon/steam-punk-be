package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record WishListDeleteResponse(
        @JsonProperty("is_deleted")
        boolean isDeleted,
        @JsonProperty("deleted_at")
        LocalDateTime deletedAt
) {
    public static WishListDeleteResponse ofSuccess() {
        return new WishListDeleteResponse(
                true,
                LocalDateTime.now()
        );
    }
}
