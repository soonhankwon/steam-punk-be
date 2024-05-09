package dev.steampunkauth.controller;

import dev.steampunkauth.dto.request.AuthLoginRequest;
import dev.steampunkauth.dto.response.AuthLoginResponse;
import dev.steampunkauth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request,
                                                   HttpServletResponse httpServletResponse) {
        AuthLoginResponse res = authService.login(request, httpServletResponse);
        return ResponseEntity.ok(res);
    }
}
