package no.vicx.backend.user.vm

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import no.vicx.database.user.VicxUser

@Schema(
    description = "A view model representing a user, containing essential user details like " +
            "username, name, email, and profile image status."
)
data class UserVm(
    @Schema(
        description = "The username of the user.",
        example = "johndoe",
        requiredMode = RequiredMode.REQUIRED
    )
    val username: String = "",

    @Schema(
        description = "The name of the user.",
        example = "John Doe",
        requiredMode = RequiredMode.REQUIRED
    )
    val name: String = "",

    @Schema(
        description = "The email address of the user.",
        example = "johndoe@example.com",
        requiredMode = RequiredMode.REQUIRED
    )
    val email: String = "",

    @Schema(
        description = "Indicates whether the user has a profile image.",
        example = "true",
        requiredMode = RequiredMode.REQUIRED
    )
    val hasImage: Boolean = false
) {
    companion object {
        fun fromVicxUser(user: VicxUser) = UserVm(
            username = user.username,
            name = user.name,
            email = user.email,
            hasImage = user.userImage != null
        )
    }
}