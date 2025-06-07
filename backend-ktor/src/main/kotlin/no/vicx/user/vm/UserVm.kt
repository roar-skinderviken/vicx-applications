package no.vicx.user.vm

import kotlinx.serialization.Serializable
import no.vicx.db.model.VicxUser

@Serializable
data class UserVm(
    val username: String,
    val name: String,
    val email: String,
    val hasImage: Boolean
) {
    companion object {
        fun fromVicxUser(user: VicxUser): UserVm = UserVm(
            user.username,
            user.name,
            user.email,
            user.userImage != null
        )
    }
}
