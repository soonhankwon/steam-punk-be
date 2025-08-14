package dev.steampunkauth.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.BiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserInfo 도메인 단위 테스트")
class UserInfoTest {

    @Test
    @DisplayName("비밀번호가 유효하면 예외가 발생하지 않는다.")
    void validatePasswordSuccess() {
        // given
        UserInfo userInfo = new UserInfo(1L, "encoded_password", true);
        String rawPassword = "password";
        BiFunction<String, String, Boolean> matchesFunction = (raw, encoded) -> true;

        // when & then
        assertThatCode(() -> userInfo.validatePassword(rawPassword, matchesFunction))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비밀번호가 유효하지 않으면 예외가 발생한다.")
    void validatePasswordFailure() {
        // given
        UserInfo userInfo = new UserInfo(1L, "encoded_password", true);
        String rawPassword = "wrong_password";
        BiFunction<String, String, Boolean> matchesFunction = (raw, encoded) -> false;

        // when & then
        assertThatThrownBy(() -> userInfo.validatePassword(rawPassword, matchesFunction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid password");
    }
}
