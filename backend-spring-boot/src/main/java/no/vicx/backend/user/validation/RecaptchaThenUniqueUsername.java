package no.vicx.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RecaptchaThenUniqueUsernameValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecaptchaThenUniqueUsername {
    String message() default "";
    String recaptchaMessage() default "{vicx.constraints.reCAPTCHA.message}";
    int usernameMinLength() default 4;
    String uniqueUsernameMessage() default "{vicx.constraints.username.UniqueUsername.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
