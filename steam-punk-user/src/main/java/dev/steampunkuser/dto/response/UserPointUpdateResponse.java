package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;

public record UserPointUpdateResponse(
        @JsonProperty("current_point")
        Long point
) {
    public static UserPointUpdateResponse from(User user) {
        return new UserPointUpdateResponse(
                user.getPoint()
        );
    }
}
