package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;

public record UserPhoneNumberUpdateResponse(
        @JsonProperty("is_updated")
        boolean isUpdated,
        @JsonProperty("phone_number")
        String phoneNumber
) {
    public static UserPhoneNumberUpdateResponse from(User user) {
        return new UserPhoneNumberUpdateResponse(
                true,
                user.getPhoneNumber()
        );
    }
}
