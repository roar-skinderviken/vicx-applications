package no.vicx.db.repository

import no.vicx.db.entity.UserImageEntity
import no.vicx.db.entity.VicxUserEntity
import no.vicx.db.model.VicxUser
import no.vicx.db.suspendTransaction
import no.vicx.db.table.VicxUserTable
import no.vicx.db.toModel

class UserRepository {

    suspend fun createUser(
        userModel: VicxUser
    ): VicxUser = suspendTransaction {
        require(bcryptRegex.matches(userModel.password)) { PASSWORD_MUST_BE_ENCRYPTED_MSG }

        val insertedUser = VicxUserEntity.new {
            this.username = userModel.username
            this.name = userModel.name
            this.password = userModel.password
            this.email = userModel.email
        }

        if (userModel.userImage != null) {
            UserImageEntity.new(insertedUser.id.value) {
                this.contentType = userModel.userImage.contentType
                this.imageData = userModel.userImage.imageData
            }
        }

        insertedUser.toModel()
    }

    suspend fun findByUsername(username: String): VicxUser? = suspendTransaction {
        VicxUserEntity
            .find { VicxUserTable.username eq username }
            .firstOrNull()
            ?.toModel()
    }

    suspend fun updateUser(
        id: Long,
        name: String?,
        email: String?
    ): Unit = suspendTransaction {
        VicxUserEntity.findByIdAndUpdate(id) { user ->
            user.apply {
                name?.let { this.name = it }
                email?.let { this.email = it }
            }
        }
    }

    companion object {
        private val bcryptRegex = Regex("^\\$2[ayb]?\\$\\d{2}\\$.{53}$")
        const val PASSWORD_MUST_BE_ENCRYPTED_MSG = "Password must be encrypted"
    }
}