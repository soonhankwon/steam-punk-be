package dev.steampunkuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPhoneNumberUpdateRequest(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("phone_number")
        String phoneNumber
) {
}
