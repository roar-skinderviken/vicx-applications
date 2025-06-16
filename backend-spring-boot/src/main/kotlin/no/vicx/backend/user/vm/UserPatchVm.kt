package no.vicx.backend.user.vm

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import no.vicx.backend.user.validation.AtLeastOneNotNull
import no.vicx.database.user.VicxUser

@AtLeastOneNotNull
@Schema(
    description = "Represents a partial update payload for a user. At least one field (name or email) " +
            "must be provided to apply the update. Designed for scenarios where only specific user attributes " +
            "need to be updated."
)
data class UserPatchVm(
    @Schema(
        description = "The name of the user to update. This field is optional, and only provided if it needs to be changed.",
        example = "John Doe",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    @field:Size(min = 4, max = 255)
    val name: String? = null,

    @Schema(
        description = "The email address of the user to update. This field is optional, and only provided if it needs to be changed.",
        example = "johndoe@example.com",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    @field:Email
    val email: String? = null,
) {
    @get:JsonIgnore
    val isEmpty: Boolean
        get() = name.isNullOrBlank() && email.isNullOrBlank()

    fun applyPatch(target: VicxUser): VicxUser = target.also {
        if (!name.isNullOrBlank()) {
            it.name = name
        }
        if (!email.isNullOrBlank()) {
            it.email = email
        }
    }
}
