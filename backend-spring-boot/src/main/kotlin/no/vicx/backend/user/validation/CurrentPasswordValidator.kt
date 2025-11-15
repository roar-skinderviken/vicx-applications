package no.vicx.backend.user.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import no.vicx.backend.user.service.UserService
import org.springframework.security.core.context.SecurityContextHolder

class CurrentPasswordValidator(
    private val userService: UserService,
) : ConstraintValidator<CurrentPassword, String> {
    private var passwordMinLength: Int = 0
    private var passwordMaxLength: Int = 0

    override fun initialize(constraintAnnotation: CurrentPassword) {
        passwordMinLength = constraintAnnotation.minLength
        passwordMaxLength = constraintAnnotation.maxLength
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication

        // if any validation errors that should be handled by other validators, return true
        return if (value == null ||
            value.length < passwordMinLength ||
            value.length > passwordMaxLength ||
            authentication == null
        ) {
            true
        } else {
            userService.isValidPassword(authentication.name, value)
        }
    }
}
