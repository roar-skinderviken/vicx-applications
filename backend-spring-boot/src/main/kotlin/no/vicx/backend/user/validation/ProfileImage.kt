package no.vicx.backend.user.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Constraint(validatedBy = [ProfileImageValidator::class])
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileImage(
    val message: String = "",
    val invalidFileTypeMessage: String = "{vicx.constraints.ProfileImage.type.message}",
    val invalidSizeMessage: String = "{vicx.constraints.ProfileImage.size.message}",
    val maxFileSize: Long = (50 * 1024).toLong(), // Default: 50KB
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
