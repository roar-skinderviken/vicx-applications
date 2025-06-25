package no.vicx.ktor.db.repository

import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.db.suspendTransaction
import no.vicx.ktor.db.table.VicxUserTable
import no.vicx.ktor.db.toModel
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger

class UserRepository {
    suspend fun createUser(userModel: VicxUser): VicxUser =
        suspendTransaction {
            require(bcryptRegex.matches(userModel.password)) { PASSWORD_MUST_BE_ENCRYPTED_MSG }

            val insertedUser =
                VicxUserEntity.new {
                    username = userModel.username
                    name = userModel.name
                    password = userModel.password
                    email = userModel.email
                }

            if (userModel.userImage != null) {
                UserImageEntity.new(insertedUser.id.value) {
                    contentType = userModel.userImage.contentType
                    imageData = userModel.userImage.imageData
                }
            }

            insertedUser.toModel()
        }

    suspend fun findByUsername(username: String): VicxUser? =
        suspendTransaction {
            addLogger(StdOutSqlLogger)
            VicxUserEntity
                .find { VicxUserTable.username eq username }
                .firstOrNull()
                ?.toModel()
        }

    suspend fun findIdByUsername(username: String): Long? =
        suspendTransaction {
            VicxUserTable
                .select(VicxUserTable.id)
                .where { VicxUserTable.username eq username }
                .map { it[VicxUserTable.id].value }
                .firstOrNull()
        }

    suspend fun updateUser(
        id: Long,
        name: String? = null,
        email: String? = null,
        password: String? = null,
    ): Unit =
        suspendTransaction {
            VicxUserEntity.findByIdAndUpdate(id) { user ->
                user.apply {
                    name?.let { this.name = it }
                    email?.let { this.email = it }
                    password?.let { this.password = it }
                }
            }
        }

    companion object {
        private val bcryptRegex = Regex("^\\$2[ayb]?\\$\\d{2}\\$.{53}$")
        const val PASSWORD_MUST_BE_ENCRYPTED_MSG = "Password must be encrypted"
    }
}
