package no.vicx.db.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UserImageTable : IdTable<Long>("user_image") {
    override val id: Column<EntityID<Long>> = reference("user_id", VicxUserTable.id)

    val contentType = varchar("content_type", 16)
    val imageData = binary("image_data")
}