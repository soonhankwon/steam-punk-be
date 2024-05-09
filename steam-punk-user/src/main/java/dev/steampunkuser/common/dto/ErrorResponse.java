package dev.steampunkuser.common.dto;

import dev.steampunkuser.common.exception.ApiException;

public record ErrorResponse(
        int code,
        String message
) {
    public static ErrorResponse from(ApiException exception) {
        return new ErrorResponse(
                exception.getHttpStatus().value(),
                exception.getErrorCode().getMessage()
        );
    }
}
