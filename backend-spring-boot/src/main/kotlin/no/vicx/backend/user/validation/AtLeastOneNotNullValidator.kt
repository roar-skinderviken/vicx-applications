package no.vicx.backend.user.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import no.vicx.backend.user.vm.UserPatchVm

class AtLeastOneNotNullValidator : ConstraintValidator<AtLeastOneNotNull, UserPatchVm> {
    private var defaultMessage: String = ""
    private var propertyNodeName: String = ""

    override fun initialize(constraintAnnotation: AtLeastOneNotNull) {
        defaultMessage = constraintAnnotation.message
        propertyNodeName = constraintAnnotation.propertyNodeName
    }

    override fun isValid(
        value: UserPatchVm,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isEmpty) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(defaultMessage)
                .addPropertyNode(propertyNodeName)
                .addConstraintViolation()

            return false
        }

        return true
    }
}
