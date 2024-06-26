package dev.steampunkpayment.common.exception;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
}
