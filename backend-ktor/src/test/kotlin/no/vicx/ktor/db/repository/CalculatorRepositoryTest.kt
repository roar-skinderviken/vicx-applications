package no.vicx.ktor.db.repository

import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaLocalDateTime
import no.vicx.ktor.db.entity.CalcEntryEntity
import no.vicx.ktor.db.table.CalcEntryTable
import no.vicx.ktor.util.CalculatorTestUtils.calcEntryInTest
import no.vicx.ktor.util.CalculatorTestUtils.generateTestCalcEntries
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.configureTestDb
import no.vicx.ktor.util.insertTestData
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneOffset

class CalculatorRepositoryTest {

    private lateinit var sut: CalculatorRepository

    @BeforeEach
    fun setup() = testApplication {
        clearAllMocks()
        configureTestDb()
        sut = CalculatorRepository()
    }

    @Nested
    inner class SaveTests {

        @Test
        fun `given valid CalcEntry when calling save expect returned CalcEntry`() = testApplication {
            val expectedCalcEntry = calcEntryInTest(1)

            application {
                val savedCalcEntry = runBlocking { sut.save(expectedCalcEntry) }

                assertEquals(
                    expectedCalcEntry.copy(
                        id = savedCalcEntry.id,
                        createdAt = savedCalcEntry.createdAt
                    ),
                    savedCalcEntry
                )
            }
        }

        @Test
        fun `given valid CalcEntry when calling save then expect EntityNotFoundException`() = testApplication {
            val expectedCalcEntry = calcEntryInTest(2)
            val expectedEntityId = EntityID(
                expectedCalcEntry.id,
                CalcEntryTable
            )

            mockkObject(CalcEntryEntity)

            every { CalcEntryEntity[any<EntityID<Long>>()] } throws
                    EntityNotFoundException(expectedEntityId, CalcEntryEntity)

            application {
                val thrown = runBlocking {
                    assertThrows<EntityNotFoundException> { sut.save(expectedCalcEntry) }
                }

                assertEquals(
                    "Entity CalcEntryEntity, id=${expectedCalcEntry.id} not found in the database",
                    thrown.message
                )
            }
        }
    }

    @Nested
    inner class FindAllOrderDescTests {

        @Test
        fun `given a populated calc_entry table when calling findAllOrderDesc expect result`() = testApplication {
            val expectedTotalCount = 101

            insertTestData {
                generateTestCalcEntries(expectedTotalCount).forEach { calcEntry ->
                    CalcEntryTable.insert { row ->
                        row[firstValue] = calcEntry.firstValue
                        row[secondValue] = calcEntry.secondValue
                        row[operation] = calcEntry.operation
                        row[result] = calcEntry.result
                        row[username] = calcEntry.username
                        row[createdAt] = calcEntry.createdAt.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
                    }
                }
            }

            application {
                val expectedPageSize = 10
                val (list, totalCount) = runBlocking { sut.findAllOrderDesc(10, expectedPageSize) }

                assertEquals(expectedTotalCount, totalCount)
                assertEquals(expectedPageSize, list.size)
                assertEquals(2, list.last().id, "last record in page should be the second oldest record")
            }
        }
    }

    @Nested
    inner class FindAllIdsByUsernameTests {

        @Test
        fun `given a populated calc_entry table when calling findAllOrderDesc expect result`() = testApplication {
            val expectedItemCount = 5

            insertTestData {
                generateTestCalcEntries(expectedItemCount)
                    .plus(generateTestCalcEntries(expectedItemCount, "~otherUser~"))
                    .forEach { calcEntry ->
                        CalcEntryTable.insert { row ->
                            row[firstValue] = calcEntry.firstValue
                            row[secondValue] = calcEntry.secondValue
                            row[operation] = calcEntry.operation
                            row[result] = calcEntry.result
                            row[username] = calcEntry.username
                            row[createdAt] = calcEntry.createdAt.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
                        }
                    }
            }

            application {
                val idsForUser = runBlocking { sut.findAllIdsByUsername(USERNAME_IN_TEST) }
                assertEquals(expectedItemCount, idsForUser.size)
            }
        }
    }

    @Nested
    inner class DeleteTests {

        @Test
        fun `given list of calc entry ids to delete then expect call to calculatorRepository#deleteByIdIn`() =
            testApplication {

                val calcEntry = calcEntryInTest(1L)

                insertTestData {
                    CalcEntryTable.insertAndGetId { row ->
                        row[firstValue] = calcEntry.firstValue
                        row[secondValue] = calcEntry.secondValue
                        row[operation] = calcEntry.operation
                        row[result] = calcEntry.result
                        row[username] = calcEntry.username
                        row[createdAt] = calcEntry.createdAt.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
                    }
                }

                val calcEntryIdsToDelete = listOf(1L)

                transaction {
                    runBlocking { sut.deleteByIdIn(calcEntryIdsToDelete) }
                }

                val thrown = transaction {
                    assertThrows<EntityNotFoundException> {
                        CalcEntryEntity[1L]
                    }
                }

                assertEquals("Entity CalcEntryEntity, id=1 not found in the database", thrown.message)
            }
    }
}