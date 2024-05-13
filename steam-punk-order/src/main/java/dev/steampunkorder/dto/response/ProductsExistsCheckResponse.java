package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductsExistsCheckResponse(
        @JsonProperty("is_exists")
        boolean isExists
) {
}
