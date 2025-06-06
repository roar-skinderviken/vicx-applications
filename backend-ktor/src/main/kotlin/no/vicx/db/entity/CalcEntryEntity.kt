package no.vicx.db.entity

import no.vicx.db.table.CalcEntryTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.ImmutableEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CalcEntryEntity(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : ImmutableEntityClass<Long, CalcEntryEntity>(CalcEntryTable)

    val firstValue by CalcEntryTable.firstValue
    val secondValue by CalcEntryTable.secondValue
    val operation by CalcEntryTable.operation
    val result by CalcEntryTable.result
    val username by CalcEntryTable.username
    val createdAt by CalcEntryTable.createdAt
}