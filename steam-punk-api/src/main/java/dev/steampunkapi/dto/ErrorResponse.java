package dev.steampunkapi.dto;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int code,
        String message
) {
    public static ErrorResponse from(JwtException exception) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage()
        );
    }
}
