package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;

public record UserPhoneNumberUpdateResponse(
        @JsonProperty("is_updated")
        Boolean isUpdated,
        @JsonProperty("phone_number")
        String phoneNumber
) {
    public static UserPhoneNumberUpdateResponse ofSuccess(User user) {
        return new UserPhoneNumberUpdateResponse(
                Boolean.TRUE,
                user.getPhoneNumber()
        );
    }
}
