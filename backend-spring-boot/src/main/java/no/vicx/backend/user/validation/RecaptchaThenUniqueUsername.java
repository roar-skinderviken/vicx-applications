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
    String recaptchaMessage() default "";
    int usernameMinLength() default 0;
    String uniqueUsernameMessage() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
