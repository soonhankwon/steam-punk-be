package dev.steampunkauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkauth.domain.UserInfo;

public record AuthLoginResponse(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("is_valid")
        boolean isValid

) {
    public static AuthLoginResponse of(UserInfo userInfo, String refreshToken) {
        return new AuthLoginResponse(
                userInfo.userId(),
                refreshToken,
                userInfo.isValid()
        );
    }
}
