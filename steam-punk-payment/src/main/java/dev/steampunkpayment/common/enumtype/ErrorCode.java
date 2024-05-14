package dev.steampunkpayment.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST_ORDER_USER_ID(HttpStatus.BAD_REQUEST, "주문의 유저 ID가 요청의 유저 ID와 일치하지 않습니다."),
    NOT_ENOUGH_USER_POINT(HttpStatus.BAD_REQUEST, "유저의 포인트가 충분하지 않습니다."),
    EXISTS_PAYMENT_HISTORY_BY_ORDER_ID(HttpStatus.BAD_REQUEST, "이미 결제된 주문 ID 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
