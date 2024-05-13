package dev.steampunkproduct.common.dto;

import dev.steampunkproduct.common.enumtype.ErrorCode;
import dev.steampunkproduct.common.exception.ApiException;

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
