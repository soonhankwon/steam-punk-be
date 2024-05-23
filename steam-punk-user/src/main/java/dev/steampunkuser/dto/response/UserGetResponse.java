package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;

public record UserGetResponse(
        @JsonProperty("user_id")
        Long userId,
        String password,
        @JsonProperty("is_registered")
        Boolean isRegistered
) {
    public static UserGetResponse ofUnRegistered() {
        return new UserGetResponse(null,
                null,
                Boolean.FALSE
        );
    }

    public static UserGetResponse ofRegistered(User user) {
        return new UserGetResponse(
                user.getId(),
                user.getPassword(),
                Boolean.TRUE
        );
    }
}
