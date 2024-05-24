package dev.steampunkuser.common.validator;

import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<EmailCheck, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email can't null or empty");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.EXISTS_DUPLICATED_EMAIL);
        }
        return true;
    }
}
