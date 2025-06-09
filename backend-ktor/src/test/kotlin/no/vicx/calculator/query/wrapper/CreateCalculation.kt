package no.vicx.calculator.query.wrapper

import kotlinx.serialization.Serializable
import no.vicx.calculator.vm.CalcVm

@Serializable
data class CreateCalculation(
    val createCalculation: CalcVm
)