package dev.steampunkuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.common.util.EmailCheck;

public record UserAddRequest(
        @EmailCheck
        String email,
        String password,
        @JsonProperty("phone_number")
        String phoneNumber
) {
}
