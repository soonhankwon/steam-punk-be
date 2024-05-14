package dev.steampunkpayment.common.dto;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;

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
