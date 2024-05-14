package dev.steampunkgame.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserGamePlayRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_id")
        Long productId
) {
}
