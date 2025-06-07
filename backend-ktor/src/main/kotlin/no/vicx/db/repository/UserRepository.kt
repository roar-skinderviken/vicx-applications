package no.vicx.db.repository

import no.vicx.db.entity.UserImageEntity
import no.vicx.db.entity.VicxUserEntity
import no.vicx.db.model.VicxUser
import no.vicx.db.suspendTransaction
import no.vicx.db.table.VicxUserTable
import no.vicx.db.toModel
import java.util.regex.Pattern

class UserRepository {

    suspend fun createUser(
        userModel: VicxUser
    ): VicxUser = suspendTransaction {
        require(BCRYPT_PATTERN.matcher(userModel.password).matches()) { PASSWORD_MUST_BE_ENCRYPTED }

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

    companion object {
        private val BCRYPT_PATTERN: Pattern = Pattern.compile("^\\$2[ayb]?\\$\\d{2}\\$.{53}$")
        const val PASSWORD_MUST_BE_ENCRYPTED = "Password must be encrypted"
    }
}