package no.vicx.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLResponseBody<out T : Any>(
    val data: T? = null,
    val errors: List<GraphqlError>? = null,
)