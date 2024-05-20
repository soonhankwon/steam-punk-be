package dev.steampunkproductstock.common.exception;

import dev.steampunkproductstock.common.enumtype.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
}
