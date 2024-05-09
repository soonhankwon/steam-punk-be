package dev.steampunkuser.controller;

import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.response.UserAddResponse;
import dev.steampunkuser.dto.response.UserPhoneNumberUpdateResponse;
import dev.steampunkuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserAddResponse> addUser(@Valid @RequestBody UserAddRequest request) {
        UserAddResponse res = userService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(res);
    }

    @PatchMapping("/phones")
    public ResponseEntity<UserPhoneNumberUpdateResponse> updatePhoneNumber(
            @RequestBody UserPhoneNumberUpdateRequest request) {
        UserPhoneNumberUpdateResponse res = userService.updatePhoneNumber(request);
        return ResponseEntity.ok(res);
    }
}
