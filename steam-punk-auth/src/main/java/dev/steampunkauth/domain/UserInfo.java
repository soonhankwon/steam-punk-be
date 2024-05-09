package dev.steampunkauth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.function.BiFunction;

public record UserInfo(
        @JsonProperty("user_id")
        Long userId,
        String password,
        @JsonProperty("is_valid")
        boolean isValid
) {
    public void validatePassword(String rawPassword, BiFunction<String, String, Boolean> matchesFunction) {
        boolean isValid = matchesFunction.apply(rawPassword, this.password);
        if (!isValid) {
            throw new IllegalArgumentException("invalid password");
        }
    }
}
