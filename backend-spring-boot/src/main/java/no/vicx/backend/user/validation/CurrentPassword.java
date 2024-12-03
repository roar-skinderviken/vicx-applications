package no.vicx.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CurrentPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPassword {
    String message() default "{vicx.constraints.CurrentPassword.message}";
    int minLength() default 1;
    int maxLength() default 1000;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
