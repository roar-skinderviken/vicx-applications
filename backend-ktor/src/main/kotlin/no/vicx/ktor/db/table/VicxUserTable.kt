package no.vicx.ktor.db.table

import org.jetbrains.exposed.dao.id.LongIdTable

object VicxUserTable: LongIdTable("vicx_user") {
    val username = varchar("username", 255).uniqueIndex("unique_username_ci")
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val email = varchar("email", 255)
}