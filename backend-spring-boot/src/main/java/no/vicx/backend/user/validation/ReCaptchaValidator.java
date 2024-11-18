package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import no.vicx.backend.user.service.RecaptchaService;

public class ReCaptchaValidator implements ConstraintValidator<ReCaptcha, String> {

    private final RecaptchaService recaptchaService;

    public ReCaptchaValidator(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // let other validators handle this
        }
        return recaptchaService.verifyToken(value);
    }
}
