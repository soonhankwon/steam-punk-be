package dev.steampunkuser.controller;

import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.request.UserPointUpdateRequest;
import dev.steampunkuser.dto.response.UserAddResponse;
import dev.steampunkuser.dto.response.UserGetResponse;
import dev.steampunkuser.dto.response.UserPasswordUpdateResponse;
import dev.steampunkuser.dto.response.UserPhoneNumberUpdateResponse;
import dev.steampunkuser.dto.response.UserPointGetResponse;
import dev.steampunkuser.dto.response.UserPointUpdateResponse;
import dev.steampunkuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{email}")
    public ResponseEntity<UserGetResponse> getUser(@PathVariable String email) {
        UserGetResponse res = userService.findUser(email);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/point/{userId}")
    public ResponseEntity<UserPointGetResponse> getUserPoint(@PathVariable Long userId) {
        UserPointGetResponse res = userService.findUserPoint(userId);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/point/{userId}/decrease")
    public ResponseEntity<UserPointUpdateResponse> decreaseUserPoint(@PathVariable Long userId,
                                                                     @RequestBody UserPointUpdateRequest request) {
        UserPointUpdateResponse res = userService.decreaseUserPoint(userId, request);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/point/{userId}/increase")
    public ResponseEntity<UserPointUpdateResponse> increaseUserPoint(@PathVariable Long userId,
                                                                     @RequestBody UserPointUpdateRequest request) {
        UserPointUpdateResponse res = userService.increaseUserPoint(userId, request);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/phones")
    public ResponseEntity<UserPhoneNumberUpdateResponse> updatePhoneNumber(
            @RequestBody UserPhoneNumberUpdateRequest request) {
        UserPhoneNumberUpdateResponse res = userService.updatePhoneNumber(request);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserPasswordUpdateResponse> updatePassword(
            @RequestBody UserPasswordUpdateRequest request) {
        UserPasswordUpdateResponse res = userService.updatePassword(request);
        return ResponseEntity.ok(res);
    }
}
