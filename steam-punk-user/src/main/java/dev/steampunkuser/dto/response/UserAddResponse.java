package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserAddResponse(
        @JsonProperty("is_added")
        Boolean isAdded
) {
    public static UserAddResponse ofSuccess() {
        return new UserAddResponse(
                Boolean.TRUE
        );
    }
}
