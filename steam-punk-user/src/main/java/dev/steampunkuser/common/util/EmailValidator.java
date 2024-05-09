package dev.steampunkuser.common.util;

import dev.steampunkuser.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<EmailCheck, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email can't null or empty");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("exists email");
        }
        return true;
    }
}
