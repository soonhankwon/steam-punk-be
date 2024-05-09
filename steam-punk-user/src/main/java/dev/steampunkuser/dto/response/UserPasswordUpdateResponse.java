package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPasswordUpdateResponse(
        @JsonProperty("is_updated")
        boolean isUpdated
) {
    public static UserPasswordUpdateResponse success() {
        return new UserPasswordUpdateResponse(
                true
        );
    }
}
