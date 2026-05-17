package no.vicx.ktor.db.table

import no.vicx.ktor.db.model.CalculatorOperation
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object CalcEntryTable : LongIdTable("calc_entry") {
    val firstValue = long("first_value")
    val secondValue = long("second_value")
    val operation = enumeration("operation", CalculatorOperation::class)
    val result = long("result")
    val username = varchar("username", 255).nullable()
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
}
