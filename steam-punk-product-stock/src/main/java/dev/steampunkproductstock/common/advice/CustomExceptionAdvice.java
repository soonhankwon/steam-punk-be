package dev.steampunkproductstock.common.advice;

import dev.steampunkproductstock.common.dto.ErrorResponse;
import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(ApiException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn(
                String.format(
                        "http-status={%d} msg={%s}",
                        errorCode.getHttpStatus().value(), errorCode.getMessage()
                )
        );
        ErrorResponse res = ErrorResponse.from(exception);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(res);
    }
}
