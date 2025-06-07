package no.vicx.extensions

import io.ktor.http.content.*
import io.ktor.utils.io.*
import no.vicx.db.model.UserImage
import no.vicx.user.vm.CreateUserVm

const val USERNAME_PART = "username"
const val NAME_PART = "name"
const val EMAIL_PART = "email"
const val PASSWORD_PART = "password"
const val RECAPTCHA_PART = "recaptchaToken"
const val IMAGE_PART = "image"

suspend fun MultiPartData.toCreateUserVm(): CreateUserVm {
    val parts = mutableMapOf<String, String>()
    this.forEachPart { part ->
        if (part is PartData.FormItem) {
            part.name?.let { parts[it] = part.value }
            part.dispose()
        }
    }

    return CreateUserVm(
        username = parts[USERNAME_PART].orEmpty(),
        name = parts[NAME_PART].orEmpty(),
        email = parts[EMAIL_PART].orEmpty(),
        password = parts[PASSWORD_PART].orEmpty(),
        recaptchaToken = parts[RECAPTCHA_PART].orEmpty()
    )
}

suspend fun MultiPartData.toUserImage(): UserImage? {
    var userImage: UserImage? = null
    this.forEachPart {
        if (it is PartData.FileItem && it.name == IMAGE_PART) {
            userImage = UserImage(
                contentType = it.contentType.toString(),
                imageData = it.provider.invoke().toByteArray()
            )
            it.dispose()
            return@forEachPart
        }
    }
    return userImage
}
