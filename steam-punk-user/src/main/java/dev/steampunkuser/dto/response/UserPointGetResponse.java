package dev.steampunkuser.dto.response;

import dev.steampunkuser.domain.User;

public record UserPointGetResponse(
        Long point
) {
    public static UserPointGetResponse from(User user) {
        return new UserPointGetResponse(
                user.getPoint()
        );
    }
}
