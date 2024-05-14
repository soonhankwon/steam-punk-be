package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;
import java.time.LocalDateTime;

public record UserPointUpdateResponse(
        Long point,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public static UserPointUpdateResponse from(User user) {
        return new UserPointUpdateResponse(
                user.getPoint(),
                LocalDateTime.now()
        );
    }
}
