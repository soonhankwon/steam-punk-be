package dev.steampunkorder.common.advice;

import dev.steampunkorder.common.dto.ErrorResponse;
import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
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
