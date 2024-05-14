package dev.steampunkgame.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EXISTS_USER_GAME_HISTORY(HttpStatus.CONFLICT, "유저의 게임 이력이 이미 존재합니다."),
    NOT_EXISTS_USER_GAME_HISTORY(HttpStatus.BAD_REQUEST, "유저의 게임 이력이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
