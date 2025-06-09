package no.vicx.ktor.calculator

import no.vicx.ktor.calculator.vm.CalcVm
import no.vicx.ktor.db.model.CalcEntry

fun CalcEntry.toGraphQLModel() = CalcVm(
    id.toInt(),
    firstValue.toInt(),
    secondValue.toInt(),
    operation.toString(),
    result.toInt(),
    username,
    createdAt.toString()
)
