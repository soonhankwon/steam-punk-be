package dev.steampunkproduct.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_EXIST_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
