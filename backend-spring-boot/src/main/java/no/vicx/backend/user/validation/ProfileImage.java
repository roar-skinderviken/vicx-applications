package no.vicx.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProfileImageValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileImage {

    String message() default "{vicx.constraints.image.ProfileImage.default.message}";
    String invalidFileTypeMessage() default "{vicx.constraints.image.ProfileImage.type.message}";
    String invalidSizeMessage() default "{vicx.constraints.image.ProfileImage.size.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long maxFileSize() default 50 * 1024; // Default: 50KB
}
