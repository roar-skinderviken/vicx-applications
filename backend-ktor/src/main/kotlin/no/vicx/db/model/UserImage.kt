package no.vicx.db.model

import kotlinx.serialization.Serializable

@Serializable
@Suppress("ArrayInDataClass")
data class UserImage(
    val id: Long = 0L,
    val contentType: String,
    val imageData: ByteArray
)
