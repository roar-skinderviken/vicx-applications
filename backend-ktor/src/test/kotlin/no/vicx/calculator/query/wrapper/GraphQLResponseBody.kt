package no.vicx.calculator.query.wrapper

import kotlinx.serialization.Serializable
import no.vicx.calculator.vm.CalcVm
import no.vicx.calculator.vm.PaginatedCalculations

@Serializable
data class GraphQLResponseBody<T>(
    val data: T? = null,
    val errors: List<GraphqlError>? = null,
)

@Serializable
data class GraphqlErrorLocation(
    val line: Int,
    val column: Int,
)

@Serializable
data class GraphqlError (
    val message: String,
    val locations: List<GraphqlErrorLocation>? = null,
    val path: List<String>? = null,
    val extensions: Extension? = null,
)

@Serializable
data class Extension(
    val data: String? = null,
)

@Serializable
data class GetAllCalculations(
    val getAllCalculations: PaginatedCalculations
)

@Serializable
data class CreateCalculation(
    val createCalculation: CalcVm
)

@Serializable
data class DeleteCalculations(
    val deleteCalculations: Boolean
)