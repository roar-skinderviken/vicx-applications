package no.vicx.ktor.db.entity

import no.vicx.ktor.db.table.UserImageTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class UserImageEntity(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<UserImageEntity>(UserImageTable)

    var contentType by UserImageTable.contentType
    var imageData by UserImageTable.imageData
}
