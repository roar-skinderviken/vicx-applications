package no.vicx.db.entity

import no.vicx.db.table.UserImageTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserImageEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserImageEntity>(UserImageTable)

    var contentType by UserImageTable.contentType
    var imageData by UserImageTable.imageData
}