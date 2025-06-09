package no.vicx.calculator.query.wrapper

import kotlinx.serialization.Serializable

@Serializable
data class DeleteCalculations(
    val deleteCalculations: Boolean
)