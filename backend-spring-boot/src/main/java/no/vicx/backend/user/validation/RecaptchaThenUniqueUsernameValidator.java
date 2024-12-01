package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import no.vicx.backend.user.service.RecaptchaService;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.UserRepository;

public class RecaptchaThenUniqueUsernameValidator implements ConstraintValidator<RecaptchaThenUniqueUsername, UserVm> {

    private final RecaptchaService recaptchaService;
    private final UserRepository userRepository;

    public RecaptchaThenUniqueUsernameValidator(RecaptchaService recaptchaService, UserRepository userRepository) {
        this.recaptchaService = recaptchaService;
        this.userRepository = userRepository;
    }

    private String recaptchaMessage;
    private int usernameMinLength;
    private String uniqueUsernameMessage;

    @Override
    public void initialize(RecaptchaThenUniqueUsername constraintAnnotation) {
        recaptchaMessage = constraintAnnotation.recaptchaMessage();
        usernameMinLength = constraintAnnotation.usernameMinLength();
        uniqueUsernameMessage = constraintAnnotation.uniqueUsernameMessage();
    }

    @Override
    public boolean isValid(UserVm value, ConstraintValidatorContext context) {
        if (value.recaptchaToken() == null
                || value.recaptchaToken().isBlank()
                || value.username() == null
                || value.username().isBlank()
                || value.username().length() < usernameMinLength) {
            return true; // let other validators handle this
        }

        if (!recaptchaService.verifyToken(value.recaptchaToken())) {
            return setValidationError("recaptchaToken", recaptchaMessage, context);
        }

        if (userRepository.findByUsername(value.username()).isPresent()) {
            return setValidationError("username", uniqueUsernameMessage, context);
        }

        return true;
    }

    private static boolean setValidationError(
            String nodeName,
            String message,
            ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(nodeName)
                .addConstraintViolation();
        return false;
    }
}
