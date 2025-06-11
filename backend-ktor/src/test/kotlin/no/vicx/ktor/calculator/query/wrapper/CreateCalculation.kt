package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable
import no.vicx.ktor.calculator.vm.CalcVm

@Serializable
data class CreateCalculation(
    val createCalculation: CalcVm
)