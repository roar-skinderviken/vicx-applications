package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import no.vicx.backend.user.vm.UserPatchRequestVm;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, UserPatchRequestVm> {

    private String defaultMessage;
    private String propertyNodeName;

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
        propertyNodeName = constraintAnnotation.propertyNodeName();
    }

    @Override
    public boolean isValid(UserPatchRequestVm value, ConstraintValidatorContext context) {
        if (value.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(defaultMessage)
                    .addPropertyNode(propertyNodeName)
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
