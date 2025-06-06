package no.vicx.db.entity

import no.vicx.db.table.UserImageTable
import no.vicx.db.table.VicxUserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VicxUserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VicxUserEntity>(VicxUserTable)

    var username by VicxUserTable.username
    var name by VicxUserTable.name
    var password by VicxUserTable.password
    var email by VicxUserTable.email
    val userImage: UserImageEntity? by UserImageEntity optionalBackReferencedOn UserImageTable.id
}