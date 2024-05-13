package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductExistsCheckResponse(
        @JsonProperty("is_exists")
        boolean isExists
) {
    public static ProductExistsCheckResponse from(boolean isExists) {
        return new ProductExistsCheckResponse(isExists);
    }
}
