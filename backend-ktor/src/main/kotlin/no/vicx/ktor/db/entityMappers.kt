package no.vicx.ktor.db

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.ktor.db.entity.CalcEntryEntity
import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.model.VicxUser
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun CalcEntryEntity.toModel() = CalcEntry(
    id.value,
    firstValue,
    secondValue,
    operation,
    result,
    username,
    createdAt.toLocalDateTime().toKotlinLocalDateTime()
)

fun VicxUserEntity.toModel() = VicxUser(
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