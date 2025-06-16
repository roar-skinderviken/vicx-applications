package no.vicx.backend.user.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Constraint(validatedBy = [CurrentPasswordValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class CurrentPassword(
    val message: String = "{vicx.constraints.CurrentPassword.message}",
    val minLength: Int = 1,
    val maxLength: Int = 1000,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
