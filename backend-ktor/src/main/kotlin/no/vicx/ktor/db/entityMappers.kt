package no.vicx.ktor.db

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.ktor.db.entity.CalcEntryEntity
import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.model.VicxUser
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun CalcEntryEntity.toModel() = no.vicx.ktor.db.model.CalcEntry(
    id.value,
    firstValue,
    secondValue,
    operation,
    result,
    username,
    createdAt.toLocalDateTime().toKotlinLocalDateTime()
)

fun no.vicx.ktor.db.entity.VicxUserEntity.toModel() = VicxUser(
    id.value,
    username,
    name,
    password,
    email,
    userImage?.toModel()
)

fun UserImageEntity.toModel() = UserImage(
    id.value,
    contentType,
    imageData
)