package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class GraphqlError (
    val message: String,
    val locations: List<GraphqlErrorLocation>? = null,
    val path: List<String>? = null,
    val extensions: Extension? = null,
)