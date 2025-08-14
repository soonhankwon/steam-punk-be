package dev.steampunkuser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.domain.User;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.request.UserPointUpdateRequest;
import dev.steampunkuser.dto.response.UserGetResponse;
import dev.steampunkuser.dto.response.UserPointGetResponse;
import dev.steampunkuser.dto.response.UserPointUpdateResponse;
import dev.steampunkuser.repository.UserRepository;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        Function<String, String> encoder = (password) -> "encoded_" + password;
        user = User.of(new UserAddRequest("test@test.com", "password", "010-1234-5678"), encoder);
    }

    @Test
    @DisplayName("신규 유저를 추가한다.")
    void addUser() {
        // given
        UserAddRequest request = new UserAddRequest("new@test.com", "new_password", "010-1111-2222");
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        userService.addUser(request);

        // then
        verify(passwordEncoder).encode("new_password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이메일로 유저를 조회한다. (등록된 유저)")
    void findUserRegistered() {
        // given
        String email = "test@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        UserGetResponse response = userService.findUser(email);

        // then
        assertThat(response.isRegistered()).isTrue();
    }

    @Test
    @DisplayName("이메일로 유저를 조회한다. (미등록 유저)")
    void findUserUnRegistered() {
        // given
        String email = "non-exists@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when
        UserGetResponse response = userService.findUser(email);

        // then
        assertThat(response.isRegistered()).isFalse();
    }

    @Nested
    @DisplayName("ID로 유저 조회 실패 테스트")
    class FindUserByIdFailure {
        @Test
        @DisplayName("존재하지 않는 유저 ID로 조회 시 예외 발생")
        void findByIdFailure() {
            // given
            Long nonExistUserId = -1L;
            given(userRepository.findById(nonExistUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updatePhoneNumber(new UserPhoneNumberUpdateRequest(nonExistUserId, "")))
                    .isInstanceOf(ApiException.class)
                    .extracting(ex -> ((ApiException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_EXISTS_USER_ID);
        }
    }

    @Nested
    @DisplayName("유저 정보 수정 테스트")
    class UpdateUserTest {
        @BeforeEach
        void setUp() {
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        }

        @Test
        @DisplayName("전화번호를 수정한다.")
        void updatePhoneNumber() {
            // given
            UserPhoneNumberUpdateRequest request = new UserPhoneNumberUpdateRequest(1L, "010-9999-8888");

            // when
            userService.updatePhoneNumber(request);

            // then
            assertThat(user.getPhoneNumber()).isEqualTo("010-9999-8888");
        }

        @Test
        @DisplayName("비밀번호를 수정한다.")
        void updatePassword() {
            // given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(1L, "new_password");
            given(passwordEncoder.encode(anyString())).willReturn("encoded_new_password");

            // when
            userService.updatePassword(request);

            // then
            verify(passwordEncoder).encode("new_password");
            assertThat(user.getPassword()).isEqualTo("encoded_new_password");
        }
    }

    @Nested
    @DisplayName("유저 포인트 테스트")
    class UserPointTest {
        @BeforeEach
        void setUp() {
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        }

        @Test
        @DisplayName("유저 포인트를 조회한다.")
        void findUserPoint() {
            // when
            UserPointGetResponse response = userService.findUserPoint(1L);

            // then
            assertThat(response.point()).isZero();
        }

        @Test
        @DisplayName("유저 포인트를 증가시킨다.")
        void increaseUserPoint() {
            // given
            UserPointUpdateRequest request = new UserPointUpdateRequest(1000L);

            // when
            UserPointUpdateResponse response = userService.increaseUserPoint(1L, request);

            // then
            assertThat(response.point()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("유저 포인트를 감소시킨다.")
        void decreaseUserPoint() {
            // given
            user.increasePoint(new UserPointUpdateRequest(1000L));
            UserPointUpdateRequest request = new UserPointUpdateRequest(300L);

            // when
            UserPointUpdateResponse response = userService.decreaseUserPoint(1L, request);

            // then
            assertThat(response.point()).isEqualTo(700L);
        }
    }
}
