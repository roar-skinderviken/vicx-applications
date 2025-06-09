package no.vicx.ktor.db.repository

import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.suspendTransaction
import no.vicx.ktor.db.table.UserImageTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class UserImageRepository {

    suspend fun saveUserImage(
        userImageModel: UserImage
    ): Unit = suspendTransaction {
        UserImageEntity.new(userImageModel.id) {
            contentType = userImageModel.contentType
            imageData = userImageModel.imageData
        }
    }

    suspend fun updateUserImage(
        userImageModel: UserImage
    ): Unit = suspendTransaction {
        UserImageEntity.findByIdAndUpdate(userImageModel.id) { userImageToUpdate ->
            userImageToUpdate.apply {
                imageData = userImageModel.imageData
                contentType = userImageModel.contentType
            }
        }
    }

    suspend fun deleteById(
        id: Long
    ): Unit = suspendTransaction {
        UserImageTable.deleteWhere { UserImageTable.id eq id }
    }
}