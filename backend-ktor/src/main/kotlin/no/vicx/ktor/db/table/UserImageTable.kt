package no.vicx.ktor.db.table

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

object UserImageTable : IdTable<Long>("user_image") {
    override val id: Column<EntityID<Long>> = reference("user_id", VicxUserTable.id)

    val contentType = varchar("content_type", 16)
    val imageData = binary("image_data")
}
