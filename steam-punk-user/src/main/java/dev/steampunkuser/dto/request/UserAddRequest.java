package dev.steampunkuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserAddRequest(
        String email,
        String password,
        @JsonProperty("phone_number")
        String phoneNumber
) {
}
