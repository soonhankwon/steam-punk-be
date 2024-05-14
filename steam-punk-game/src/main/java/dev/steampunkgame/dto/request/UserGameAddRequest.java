package dev.steampunkgame.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserGameAddRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_ids")
        List<Long> productIds
) {
}
