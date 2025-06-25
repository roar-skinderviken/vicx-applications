package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class Extension(
    val data: String? = null,
)
