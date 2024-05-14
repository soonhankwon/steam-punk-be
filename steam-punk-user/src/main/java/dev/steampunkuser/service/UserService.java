package dev.steampunkuser.service;

import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.domain.User;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.response.UserAddResponse;
import dev.steampunkuser.dto.response.UserGetResponse;
import dev.steampunkuser.dto.response.UserPasswordUpdateResponse;
import dev.steampunkuser.dto.response.UserPhoneNumberUpdateResponse;
import dev.steampunkuser.dto.response.UserPointGetResponse;
import dev.steampunkuser.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAddResponse addUser(UserAddRequest request) {
        User user = User.of(request, passwordEncoder::encode);
        user = userRepository.save(user);
        return UserAddResponse.from(user);
    }

    @Transactional
    public UserPhoneNumberUpdateResponse updatePhoneNumber(UserPhoneNumberUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));

        user.updatePhoneNumber(request);
        return UserPhoneNumberUpdateResponse.from(user);
    }

    @Transactional
    public UserPasswordUpdateResponse updatePassword(UserPasswordUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));
        user.updatePassword(request, passwordEncoder::encode);
        return UserPasswordUpdateResponse.success();
    }

    @Transactional(readOnly = true)
    public UserGetResponse findUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return UserGetResponse.invalid();
        }
        User user = optionalUser.get();
        return UserGetResponse.valid(user);
    }

    @Transactional(readOnly = true)
    public UserPointGetResponse findUserPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_ID));
        return UserPointGetResponse.from(user);
    }
}
