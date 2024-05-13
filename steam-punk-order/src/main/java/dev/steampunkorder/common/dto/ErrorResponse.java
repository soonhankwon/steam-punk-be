package dev.steampunkorder.common.dto;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;

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
