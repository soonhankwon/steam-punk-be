package dev.steampunkuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPasswordUpdateRequest(
        @JsonProperty("user_id")
        Long userId,
        String password
) {
}
