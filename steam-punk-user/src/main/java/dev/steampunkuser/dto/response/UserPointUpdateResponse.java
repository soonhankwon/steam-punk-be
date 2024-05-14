package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;
import java.time.LocalDateTime;

public record UserPointUpdateResponse(
        @JsonProperty("is_updated")
        boolean isUpdated,
        Long point,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public static UserPointUpdateResponse from(User user) {
        return new UserPointUpdateResponse(
                true,
                user.getPoint(),
                LocalDateTime.now()
        );
    }
}
