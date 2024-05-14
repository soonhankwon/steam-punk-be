package dev.steampunkorder.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_EXISTS_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID가 존재하지 않습니다."),
    EXISTS_PRODUCT_ID_IN_WISHLIST(HttpStatus.BAD_REQUEST, "상품 ID가 위시리스트에 존재합니다."),
    NOT_EXISTS_PRODUCT_ID_BY_USER(HttpStatus.BAD_REQUEST, "유저 위시리스트에 상품 ID가 존재하지 않습니다."),
    NOT_EXISTS_ORDER_ID(HttpStatus.BAD_REQUEST, "주문 ID가 존재하지 않습니다."),
    NOT_EXISTS_ORDER_ID_BY_USER(HttpStatus.BAD_REQUEST, "유저의 해당 주문 ID가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
