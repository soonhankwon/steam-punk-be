package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;

public record UserGetResponse(
        @JsonProperty("user_id")
        Long userId,
        String password,
        @JsonProperty("is_valid")
        boolean isValid
) {
    public static UserGetResponse invalid() {
        return new UserGetResponse(null,
                null,
                false
        );
    }

    public static UserGetResponse valid(User user) {
        return new UserGetResponse(
                user.getId(),
                user.getPassword(),
                true
        );
    }
}
