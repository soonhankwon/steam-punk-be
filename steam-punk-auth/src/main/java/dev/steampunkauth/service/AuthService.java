package dev.steampunkauth.service;

import dev.steampunkauth.common.util.JwtProvider;
import dev.steampunkauth.domain.UserInfo;
import dev.steampunkauth.dto.request.AuthLoginRequest;
import dev.steampunkauth.dto.response.AuthLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthLoginResponse login(AuthLoginRequest request, HttpServletResponse httpServletResponse) {
        String defaultAccessToken = jwtProvider.createDefaultToken(true);
        UserInfo userInfo = getUserInfo(request, defaultAccessToken);

        if (!userInfo.isRegistered()) {
            throw new IllegalArgumentException("invalid access");
        }

        userInfo.validatePassword(request.password(), passwordEncoder::matches);
        Long userId = userInfo.userId();
        String accessToken = jwtProvider.createJwt(userId, true);
        httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        String refreshToken = jwtProvider.createJwt(userId, false);
        return AuthLoginResponse.of(userInfo, refreshToken);
    }

    private UserInfo getUserInfo(AuthLoginRequest request, String accessToken) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/users/" + request.email())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }
}
