package dev.steampunkuser.service;

import dev.steampunkuser.domain.User;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.response.UserAddResponse;
import dev.steampunkuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserAddResponse addUser(UserAddRequest request) {
        User user = User.from(request);
        user = userRepository.save(user);
        return UserAddResponse.from(user);
    }
}
