package dev.steampunkuser.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkuser.domain.User;
import java.time.LocalDateTime;

public record UserAddResponse(
        @JsonProperty("is_added")
        boolean isAdded,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static UserAddResponse from(User user) {
        return new UserAddResponse(
                true,
                user.getCreatedAt()
        );
    }
}
