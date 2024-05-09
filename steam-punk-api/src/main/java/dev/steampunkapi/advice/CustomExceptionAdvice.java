package dev.steampunkapi.advice;

import dev.steampunkapi.dto.ErrorResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> handleJwtException(JwtException exception) {
        log.warn(
                String.format(
                        "http-status={%d} msg={%s}",
                        HttpStatus.UNAUTHORIZED.value(), exception.getMessage()
                )
        );
        ErrorResponse res = ErrorResponse.from(exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }
}
