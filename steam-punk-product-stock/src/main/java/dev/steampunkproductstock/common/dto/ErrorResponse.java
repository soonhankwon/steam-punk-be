package dev.steampunkproductstock.common.dto;

import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;

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
