package no.vicx.backend.user.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Constraint(validatedBy = [RecaptchaThenUniqueUsernameValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class RecaptchaThenUniqueUsername(
    val message: String = "",
    val recaptchaMessage: String = "{vicx.constraints.reCAPTCHA.message}",
    val usernameMinLength: Int = 4,
    val uniqueUsernameMessage: String = "{vicx.constraints.username.UniqueUsername.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
