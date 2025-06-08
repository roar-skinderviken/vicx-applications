package no.vicx.extension

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

suspend fun MultiPartData.toCreateUserVmAndUserImage(): Pair<CreateUserVm, UserImage?> {
    val formItems = mutableMapOf<String, String>()
    var userImage: UserImage? = null

    this.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> part.name?.let { formItems[it] = part.value }
            is PartData.FileItem -> if (part.name == IMAGE_PART) {
                userImage = UserImage(
                    contentType = part.contentType?.toString().orEmpty(),
                    imageData = part.provider.invoke().toByteArray()
                )
            }

            else -> Unit
        }
        part.dispose()
    }

    return CreateUserVm(
        username = formItems[USERNAME_PART].orEmpty(),
        name = formItems[NAME_PART].orEmpty(),
        email = formItems[EMAIL_PART].orEmpty(),
        password = formItems[PASSWORD_PART].orEmpty(),
        recaptchaToken = formItems[RECAPTCHA_PART].orEmpty()
    ) to userImage
}