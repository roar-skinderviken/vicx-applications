package no.vicx.ktor.db.entity

import no.vicx.ktor.db.table.CalcEntryTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.ImmutableEntityClass

class CalcEntryEntity(
    id: EntityID<Long>,
) : Entity<Long>(id) {
    companion object : ImmutableEntityClass<Long, CalcEntryEntity>(CalcEntryTable)

    val firstValue by CalcEntryTable.firstValue
    val secondValue by CalcEntryTable.secondValue
    val operation by CalcEntryTable.operation
    val result by CalcEntryTable.result
    val username by CalcEntryTable.username
    val createdAt by CalcEntryTable.createdAt
}
