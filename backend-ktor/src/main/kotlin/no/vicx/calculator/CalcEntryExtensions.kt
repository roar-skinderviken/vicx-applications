package no.vicx.calculator

import no.vicx.calculator.vm.CalcVm
import no.vicx.db.model.CalcEntry

fun CalcEntry.toGraphQLModel() = CalcVm(
    id.toInt(),
    firstValue.toInt(),
    secondValue.toInt(),
    operation.toString(),
    result.toInt(),
    username,
    createdAt.toString()
)
