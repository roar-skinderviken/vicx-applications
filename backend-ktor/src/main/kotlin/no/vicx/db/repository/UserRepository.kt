package no.vicx.db.repository

import no.vicx.db.entity.UserImageEntity
import no.vicx.db.entity.VicxUserEntity
import no.vicx.db.model.VicxUser
import no.vicx.db.suspendTransaction
import no.vicx.db.toModel

class UserRepository {

    suspend fun createUser(
        userModel: VicxUser
    ): VicxUser = suspendTransaction {
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
}