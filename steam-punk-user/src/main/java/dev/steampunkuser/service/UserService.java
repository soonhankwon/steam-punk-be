package dev.steampunkuser.service;

import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.domain.User;
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
import dev.steampunkuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAddResponse addUser(@RequestBody UserAddRequest request) {
        User user = User.of(request, passwordEncoder::encode);
        userRepository.save(user);
        return UserAddResponse.ofSuccess();
    }

    @Transactional
    public UserPhoneNumberUpdateResponse updatePhoneNumber(@RequestBody UserPhoneNumberUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));

        user.updatePhoneNumber(request);
        return UserPhoneNumberUpdateResponse.ofSuccess(user);
    }

    @Transactional
    public UserPasswordUpdateResponse updatePassword(@RequestBody UserPasswordUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));
        user.updatePassword(request, passwordEncoder::encode);
        return UserPasswordUpdateResponse.ofSuccess();
    }

    @Transactional(readOnly = true)
    public UserGetResponse findUser(String email) {
        return userRepository.findByEmail(email)
                .map(UserGetResponse::ofRegistered)
                .orElseGet(UserGetResponse::ofUnRegistered);
    }

    @Transactional(readOnly = true)
    public UserPointGetResponse findUserPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));
        return UserPointGetResponse.from(user);
    }

    @Transactional
    public UserPointUpdateResponse increaseUserPoint(Long userId,
                                                     @RequestBody UserPointUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));

        user.increasePoint(request);
        return UserPointUpdateResponse.from(user);
    }

    @Transactional
    public UserPointUpdateResponse decreaseUserPoint(Long userId,
                                                     @RequestBody UserPointUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));

        user.decreasePoint(request);
        return UserPointUpdateResponse.from(user);
    }
}
