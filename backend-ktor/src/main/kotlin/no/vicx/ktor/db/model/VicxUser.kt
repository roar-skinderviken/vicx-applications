package no.vicx.ktor.db.model

import kotlinx.serialization.Serializable

@Serializable
data class VicxUser(
    val id: Long = 0L,
    val username: String,
    val name: String,
    val password: String,
    val email: String,
    val userImage: UserImage? = null,
)
