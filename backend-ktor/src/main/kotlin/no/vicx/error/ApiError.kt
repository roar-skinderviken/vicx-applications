package no.vicx.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val timestamp: Long = System.currentTimeMillis(),
    val status: Int,
    val message: String,
    val url: String,
    val validationErrors: Map<String, String> = emptyMap()
)