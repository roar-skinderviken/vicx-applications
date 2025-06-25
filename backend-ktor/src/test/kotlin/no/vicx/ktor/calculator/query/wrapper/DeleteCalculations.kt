package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class DeleteCalculations(
    val deleteCalculations: Boolean,
)
