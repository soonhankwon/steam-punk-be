package dev.steampunkuser.common.validator;

import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailCheck {
    String message() default "이메일이 유효하지 않습니다.";

    Class[] groups() default {};

    Class[] payload() default {};
}
