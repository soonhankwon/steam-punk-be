package dev.steampunkauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import dev.steampunkauth.domain.UserInfo;
import dev.steampunkauth.dto.request.AuthLoginRequest;
import dev.steampunkauth.dto.response.AuthLoginResponse;
import dev.steampunkauth.common.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인에 성공한다.")
        void loginSuccess() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("test@test.com", "password");
            HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
            UserInfo mockUserInfo = new UserInfo(1L, "encoded_password", true);
            String mockAccessToken = "mockAccessToken";
            String mockRefreshToken = "mockRefreshToken";

            given(jwtProvider.createDefaultToken(anyBoolean())).willReturn("defaultToken");
            given(passwordEncoder.matches(request.password(), mockUserInfo.password())).willReturn(true);
            given(jwtProvider.createJwt(mockUserInfo.userId(), true)).willReturn(mockAccessToken);
            given(jwtProvider.createJwt(mockUserInfo.userId(), false)).willReturn(mockRefreshToken);

            // WebClient mocking
            WebClient webClient = mock(WebClient.class);
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

            try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
                mockedWebClient.when(WebClient::create).thenReturn(webClient);
                given(webClient.get()).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.headers(any(Consumer.class))).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
                given(responseSpec.bodyToMono(UserInfo.class)).willReturn(Mono.just(mockUserInfo));

                // when
                AuthLoginResponse response = authService.login(request, httpServletResponse);

                // then
                assertThat(response.userId()).isEqualTo(mockUserInfo.userId());
                assertThat(response.refreshToken()).isEqualTo(mockRefreshToken);
                verify(httpServletResponse).setHeader(HttpHeaders.AUTHORIZATION, mockAccessToken);
            }
        }

        @Test
        @DisplayName("미가입 유저일 경우 예외가 발생한다.")
        void loginFailUnregisteredUser() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("unregistered@test.com", "password");
            HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
            UserInfo mockUserInfo = new UserInfo(null, null, false);

            given(jwtProvider.createDefaultToken(anyBoolean())).willReturn("defaultToken");

            // WebClient mocking
            WebClient webClient = mock(WebClient.class);
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

            try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
                mockedWebClient.when(WebClient::create).thenReturn(webClient);
                given(webClient.get()).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.headers(any(Consumer.class))).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
                given(responseSpec.bodyToMono(UserInfo.class)).willReturn(Mono.just(mockUserInfo));

                // when & then
                assertThatThrownBy(() -> authService.login(request, httpServletResponse))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("invalid access");
            }
        }

        @Test
        @DisplayName("비밀번호가 틀릴 경우 예외가 발생한다.")
        void loginFailInvalidPassword() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("test@test.com", "wrong_password");
            HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
            UserInfo mockUserInfo = new UserInfo(1L, "encoded_password", true);

            given(jwtProvider.createDefaultToken(anyBoolean())).willReturn("defaultToken");
            given(passwordEncoder.matches(request.password(), mockUserInfo.password())).willReturn(false);

            // WebClient mocking
            WebClient webClient = mock(WebClient.class);
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

            try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
                mockedWebClient.when(WebClient::create).thenReturn(webClient);
                given(webClient.get()).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.headers(any(Consumer.class))).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
                given(responseSpec.bodyToMono(UserInfo.class)).willReturn(Mono.just(mockUserInfo));

                // when & then
                assertThatThrownBy(() -> authService.login(request, httpServletResponse))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("invalid password");
            }
        }
    }
}
