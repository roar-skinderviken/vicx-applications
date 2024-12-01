package no.vicx.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotNull {
    String message() default "{vicx.constraints.AtLeastOneNotNull.message}";
    String propertyNodeName() default "patchRequestBody";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}