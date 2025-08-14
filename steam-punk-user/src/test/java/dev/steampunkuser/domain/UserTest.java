package dev.steampunkuser.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.request.UserPointUpdateRequest;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User 도메인 단위 테스트")
class UserTest {

    private final Function<String, String> passwordEncoder = password -> "encoded_" + password;

    @Test
    @DisplayName("UserAddRequest와 인코딩 함수를 기반으로 User 객체를 생성한다.")
    void of() {
        // given
        String email = "test@example.com";
        String password = "password";
        String phoneNumber = "010-1234-5678";
        UserAddRequest request = new UserAddRequest(email, password, phoneNumber);

        // when
        User user = User.of(request, passwordEncoder);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.apply(password));
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getPoint()).isZero();
    }

    @Test
    @DisplayName("전화번호를 성공적으로 변경한다.")
    void updatePhoneNumber() {
        // given
        User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
        String newPhoneNumber = "010-9999-8888";
        UserPhoneNumberUpdateRequest request = new UserPhoneNumberUpdateRequest(1L, newPhoneNumber);

        // when
        user.updatePhoneNumber(request);

        // then
        assertThat(user.getPhoneNumber()).isEqualTo(newPhoneNumber);
    }

    @Test
    @DisplayName("비밀번호를 성공적으로 변경한다.")
    void updatePassword() {
        // given
        User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
        String newPassword = "new_password";
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(1L, newPassword);

        // when
        user.updatePassword(request, passwordEncoder);

        // then
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.apply(newPassword));
    }

    @Nested
    @DisplayName("포인트 차감 테스트")
    class DecreasePointTest {

        @Test
        @DisplayName("포인트 차감에 성공한다.")
        void decreasePointSuccess() {
            // given
            User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
            user.increasePoint(new UserPointUpdateRequest(1000L)); // 초기 포인트 1000으로 설정
            UserPointUpdateRequest request = new UserPointUpdateRequest(300L);

            // when
            user.decreasePoint(request);

            // then
            assertThat(user.getPoint()).isEqualTo(700L);
        }

        @Test
        @DisplayName("차감하려는 포인트가 음수이면 예외가 발생한다.")
        void decreasePointWithNegativeValue() {
            // given
            User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
            user.increasePoint(new UserPointUpdateRequest(1000L));
            UserPointUpdateRequest request = new UserPointUpdateRequest(-100L);

            // when & then
            assertThatThrownBy(() -> user.decreasePoint(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("음수는 허용되지 않습니다.");
        }

        @Test
        @DisplayName("보유 포인트보다 많이 차감하려고 하면 예외가 발생한다.")
        void decreasePointWithNotEnoughPoint() {
            // given
            User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
            user.increasePoint(new UserPointUpdateRequest(500L));
            UserPointUpdateRequest request = new UserPointUpdateRequest(1000L);

            // when & then
            assertThatThrownBy(() -> user.decreasePoint(request))
                    .isInstanceOf(ApiException.class)
                    .extracting(ex -> ((ApiException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_ENOUGH_POINT);
        }
    }

    @Nested
    @DisplayName("포인트 증가 테스트")
    class IncreasePointTest {

        @Test
        @DisplayName("포인트 증가에 성공한다.")
        void increasePointSuccess() {
            // given
            User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
            UserPointUpdateRequest request = new UserPointUpdateRequest(500L);

            // when
            user.increasePoint(request);

            // then
            assertThat(user.getPoint()).isEqualTo(500L);
        }

        @Test
        @DisplayName("증가하려는 포인트가 음수이면 예외가 발생한다.")
        void increasePointWithNegativeValue() {
            // given
            User user = User.of(new UserAddRequest("test@ex.com", "pass", "010-1111-2222"), passwordEncoder);
            UserPointUpdateRequest request = new UserPointUpdateRequest(-100L);

            // when & then
            assertThatThrownBy(() -> user.increasePoint(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("음수는 허용되지 않습니다.");
        }
    }
}
