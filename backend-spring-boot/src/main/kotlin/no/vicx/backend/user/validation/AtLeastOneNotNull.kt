package no.vicx.backend.user.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Constraint(validatedBy = [AtLeastOneNotNullValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class AtLeastOneNotNull(
    val message: String = "{vicx.constraints.AtLeastOneNotNull.message}",
    val propertyNodeName: String = "patchRequestBody",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)