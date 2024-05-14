package dev.steampunkuser.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EXISTS_DUPLICATED_EMAIL(HttpStatus.CONFLICT, "중복된 이메일이 존재합니다."),
    DECRYPTION_FAILED(HttpStatus.BAD_REQUEST, "복호화가 실패했습니다."),
    ENCRYPTION_FAILED(HttpStatus.BAD_REQUEST, "암호화가 실패했습니다."),
    CRYPTOGRAPHY_FAILED(HttpStatus.BAD_REQUEST, "암복호화가 실패했습니다."),
    NOT_EXISTS_USER_ID(HttpStatus.BAD_REQUEST, "유저아이디가 존재하지 않습니다."),
    TOO_LOW_POINT(HttpStatus.BAD_REQUEST, "포인트는 0이상이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
