package dev.steampunkgame.common.dto;

import dev.steampunkgame.common.enumtype.ErrorCode;
import dev.steampunkgame.common.exception.ApiException;

public record ErrorResponse(
        int code,
        String message
) {
    public static ErrorResponse from(ApiException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage()
        );
    }
}
