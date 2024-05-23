package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPasswordUpdateResponse(
        @JsonProperty("is_updated")
        Boolean isUpdated
) {
    public static UserPasswordUpdateResponse ofSuccess() {
        return new UserPasswordUpdateResponse(
                Boolean.TRUE
        );
    }
}
