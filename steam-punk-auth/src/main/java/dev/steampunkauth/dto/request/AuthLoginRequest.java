package dev.steampunkauth.dto.request;

public record AuthLoginRequest(
        String email,
        String password
) {
}
