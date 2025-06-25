package no.vicx.backend.user.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import no.vicx.backend.user.service.RecaptchaService
import no.vicx.backend.user.vm.CreateUserVm
import no.vicx.database.user.UserRepository

class RecaptchaThenUniqueUsernameValidator(
    private val recaptchaService: RecaptchaService,
    private val userRepository: UserRepository,
) : ConstraintValidator<RecaptchaThenUniqueUsername, CreateUserVm> {
    private var recaptchaMessage: String = ""
    private var usernameMinLength = 0
    private var uniqueUsernameMessage: String = ""

    override fun initialize(constraintAnnotation: RecaptchaThenUniqueUsername) {
        recaptchaMessage = constraintAnnotation.recaptchaMessage
        usernameMinLength = constraintAnnotation.usernameMinLength
        uniqueUsernameMessage = constraintAnnotation.uniqueUsernameMessage
    }

    override fun isValid(
        value: CreateUserVm,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.recaptchaToken.isBlank() ||
            value.username.isNullOrBlank() ||
            value.username.length < usernameMinLength
        ) {
            return true // let other validators handle this
        }

        if (!recaptchaService.verifyToken(value.recaptchaToken)) {
            return setValidationError("recaptchaToken", recaptchaMessage, context)
        }

        if (userRepository.findByUsername(value.username).isPresent) {
            return setValidationError("username", uniqueUsernameMessage, context)
        }

        return true
    }

    companion object {
        private fun setValidationError(
            nodeName: String,
            message: String,
            context: ConstraintValidatorContext,
        ): Boolean {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(nodeName)
                .addConstraintViolation()
            return false
        }
    }
}
