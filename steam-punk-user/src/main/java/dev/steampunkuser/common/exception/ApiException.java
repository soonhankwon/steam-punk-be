package dev.steampunkuser.common.exception;

import dev.steampunkuser.common.enumtype.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;
}
