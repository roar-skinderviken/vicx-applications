package no.vicx.ktor.util

import io.ktor.http.content.*
import io.ktor.server.plugins.requestvalidation.*
import no.vicx.ktor.user.vm.CreateUserVm
import no.vicx.ktor.util.MultiPartUtils.consumeAndValidateUserImageFilePart

const val USERNAME_PART = "username"
const val NAME_PART = "name"
const val EMAIL_PART = "email"
const val PASSWORD_PART = "password"
const val RECAPTCHA_PART = "recaptchaToken"
const val IMAGE_PART = "image"

suspend fun MultiPartData.extractFormItemsAndFileItem(
    userImageCallback: suspend (UserImageResult) -> Unit,
    userCallback: (suspend (ValidationResult, CreateUserVm) -> Unit)? = null
) {
    val capturedFormItems = mutableMapOf<String, String>()

    this.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> part.name?.also { partName -> capturedFormItems[partName] = part.value }
            is PartData.FileItem -> userImageCallback(consumeAndValidateUserImageFilePart(part))
            else -> Unit
        }
        part.dispose()
    }

    if (userCallback != null) {
        val createUserVm = CreateUserVm(
            username = capturedFormItems[USERNAME_PART].orEmpty(),
            name = capturedFormItems[NAME_PART].orEmpty(),
            email = capturedFormItems[EMAIL_PART].orEmpty(),
            password = capturedFormItems[PASSWORD_PART].orEmpty(),
            recaptchaToken = capturedFormItems[RECAPTCHA_PART].orEmpty()
        )

        userCallback(createUserVm.validate(), createUserVm)
    }
}