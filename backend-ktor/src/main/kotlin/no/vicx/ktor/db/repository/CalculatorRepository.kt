package no.vicx.ktor.db.repository

import kotlinx.datetime.toJavaLocalDateTime
import no.vicx.ktor.db.entity.CalcEntryEntity
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.suspendTransaction
import no.vicx.ktor.db.table.CalcEntryTable
import no.vicx.ktor.db.toModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CalculatorRepository {

    suspend fun save(calcEntry: CalcEntry): CalcEntry = suspendTransaction {
        CalcEntryTable.insertAndGetId { row ->
            row[firstValue] = calcEntry.firstValue
            row[secondValue] = calcEntry.secondValue
            row[operation] = calcEntry.operation
            row[result] = calcEntry.result
            row[username] = calcEntry.username
            row[createdAt] = calcEntry.createdAt.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
        }
            .let { insertedId -> CalcEntryEntity[insertedId] }
            .toModel()
    }

    suspend fun findAllOrderDesc(
        page: Int,
        size: Int
    ): Pair<List<CalcEntry>, Int> = suspendTransaction {
        val offset = (page - 1) * size

        CalcEntryEntity.all()
            .orderBy(CalcEntryTable.id to SortOrder.DESC)
            .offset(offset.toLong())
            .limit(size)
            .map { it.toModel() } to CalcEntryTable.selectAll().count().toInt()
    }

    suspend fun deleteByIdIn(
        ids: List<Long>
    ): Int = suspendTransaction {
        addLogger(StdOutSqlLogger) // TODO
        CalcEntryTable.deleteWhere { CalcEntryTable.id inList ids }
    }

    suspend fun findAllIdsByUsername(
        username: String
    ): Set<Int> = suspendTransaction {
        CalcEntryTable
            .select(CalcEntryTable.id)
            .where { CalcEntryTable.username eq username }
            .map { it[CalcEntryTable.id].value.toInt() }
            .toSet()
    }

    suspend fun deleteAllByCreatedAtBeforeAndUsernameNull(
        createdAt: OffsetDateTime
    ): Int = suspendTransaction {
        CalcEntryTable.deleteWhere {
            (username.isNull()) and (CalcEntryTable.createdAt less createdAt)
        }
    }
}