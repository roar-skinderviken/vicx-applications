package no.vicx.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProfileImageValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileImage {
    String message() default "";
    String invalidFileTypeMessage() default "{vicx.constraints.ProfileImage.type.message}";
    String invalidSizeMessage() default "{vicx.constraints.ProfileImage.size.message}";
    long maxFileSize() default 50 * 1024; // Default: 50KB

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
