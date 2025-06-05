package no.vicx.db.table

import no.vicx.db.model.CalculatorOperation
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object CalcEntryTable : LongIdTable("calc_entry") {
    val firstValue = long("first_value")
    val secondValue = long("second_value")
    val operation: Column<CalculatorOperation> = enumerationByName(
        "operation",
        length = 8,
        klass = CalculatorOperation::class
    )
    val result = long("result")
    val username = varchar("username", 255)
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
}