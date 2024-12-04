package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import no.vicx.backend.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentPasswordValidator implements ConstraintValidator<CurrentPassword, String> {

    private final UserService userService;

    public CurrentPasswordValidator(UserService userService) {
        this.userService = userService;
    }

    Integer passwordMinLength;
    Integer passwordMaxLength;

    @Override
    public void initialize(CurrentPassword constraintAnnotation) {
        passwordMinLength = constraintAnnotation.minLength();
        passwordMaxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null
                || value.length() < passwordMinLength
                || value.length() > passwordMaxLength
                || userService.isValidPassword(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                value);
    }
}
