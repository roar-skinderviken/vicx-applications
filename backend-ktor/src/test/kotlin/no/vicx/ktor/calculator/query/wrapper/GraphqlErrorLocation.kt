package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class GraphqlErrorLocation(
    val line: Int,
    val column: Int,
)
