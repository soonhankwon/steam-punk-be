package dev.steampunkproductstock.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_EXIST_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID가 존재하지 않습니다."),
    NO_STOCK_BY_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID 재고가 없습니다."),
    MINIMUM_STOCK_REACHED(HttpStatus.BAD_REQUEST, "현재 상품의 재고는 최소 수량인 0 입니다."),
    MAXIMUM_STOCK_REACHED(HttpStatus.BAD_REQUEST, "현재 상품의 재고는 최대 수량입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
