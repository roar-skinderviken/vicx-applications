package no.vicx.ktor.util

import io.ktor.server.plugins.requestvalidation.*
import no.vicx.ktor.db.model.UserImage

sealed class UserImageResult {
    data class ValidImageResult(val userImage: UserImage) : UserImageResult()
    data class InvalidImageResult(val validationError: ValidationResult.Invalid) : UserImageResult()
}