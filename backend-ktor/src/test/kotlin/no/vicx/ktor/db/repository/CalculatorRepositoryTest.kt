package no.vicx.ktor.db.repository

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.datetime.toJavaLocalDateTime
import no.vicx.ktor.db.entity.CalcEntryEntity
import no.vicx.ktor.db.table.CalcEntryTable
import no.vicx.ktor.util.CalculatorTestUtils.calcEntryInTest
import no.vicx.ktor.util.CalculatorTestUtils.generateTestCalcEntries
import no.vicx.ktor.util.MiscTestUtils.shouldBeCloseTo
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.configureTestDb
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class CalculatorRepositoryTest :
    BehaviorSpec({
        val sut = CalculatorRepository()

        Given("Calculator repository") {
            beforeContainer {
                configureTestDb()
            }

            When("saving valid data") {
                val expectedCalcEntry = calcEntryInTest(1)

                val savedCalcEntry = sut.save(expectedCalcEntry)

                Then("it should return the saved entry with the expected values") {
                    savedCalcEntry shouldBe
                        expectedCalcEntry.copy(
                            id = savedCalcEntry.id,
                            createdAt = savedCalcEntry.createdAt,
                        )
                }

                And("the createdAt timestamp should match the system time") {
                    assertSoftly(savedCalcEntry.createdAt.shouldNotBeNull()) {
                        this.toJavaLocalDateTime() shouldBeCloseTo LocalDateTime.now()
                    }
                }
            }

            When("saving data with a non-existing ID") {
                val expectedCalcEntry = calcEntryInTest(2)
                val expectedEntityId =
                    EntityID(
                        expectedCalcEntry.id,
                        CalcEntryTable,
                    )

                mockkObject(CalcEntryEntity)

                every { CalcEntryEntity[any<EntityID<Long>>()] } throws
                    EntityNotFoundException(expectedEntityId, CalcEntryEntity)

                val thrown = shouldThrow<EntityNotFoundException> { sut.save(expectedCalcEntry) }

                Then("it should throw an EntityNotFoundException with a not found message") {
                    thrown.message shouldBe "Entity CalcEntryEntity, id=${expectedCalcEntry.id} not found in the database"
                }
            }

            When("finding all entries ordered by descending date in a populated table") {
                val expectedTotalCount = 101
                val expectedPageSize = 10
                val pageNumberInTest = 10

                transaction {
                    generateTestCalcEntries(expectedTotalCount).forEach { calcEntry ->
                        CalcEntryTable.insert { row ->
                            row[firstValue] = calcEntry.firstValue
                            row[secondValue] = calcEntry.secondValue
                            row[operation] = calcEntry.operation
                            row[result] = calcEntry.result
                            row[username] = calcEntry.username
                            row[createdAt] = OffsetDateTime.now(ZoneOffset.UTC)
                        }
                    }
                }

                val (calcEntries, totalCount) = sut.findAllOrderDesc(pageNumberInTest, expectedPageSize)

                Then("it should return the total count of $expectedTotalCount") {
                    totalCount shouldBe expectedTotalCount
                }

                And("it should return $expectedPageSize entries for the page") {
                    calcEntries.size shouldBe expectedPageSize
                }

                And("the last record on page $pageNumberInTest should be the second oldest entry") {
                    calcEntries.last().id shouldBe 2
                }
            }

            When("finding all IDs by username") {
                val expectedItemCount = 5

                transaction {
                    generateTestCalcEntries(expectedItemCount)
                        .plus(generateTestCalcEntries(expectedItemCount, "~otherUser~"))
                        .forEach { calcEntry ->
                            CalcEntryTable.insert { row ->
                                row[firstValue] = calcEntry.firstValue
                                row[secondValue] = calcEntry.secondValue
                                row[operation] = calcEntry.operation
                                row[result] = calcEntry.result
                                row[username] = calcEntry.username
                                row[createdAt] = OffsetDateTime.now(ZoneOffset.UTC)
                            }
                        }
                }

                val idsForUser = sut.findAllIdsByUsername(USERNAME_IN_TEST)

                Then("it should return $expectedItemCount IDs for the specified user") {
                    idsForUser.size shouldBe expectedItemCount
                }
            }

            When("deleting entries by ID") {
                val calcEntry = calcEntryInTest(1L)
                val calcEntryIdsToDelete = listOf(1L)

                transaction {
                    CalcEntryTable.insertAndGetId { row ->
                        row[firstValue] = calcEntry.firstValue
                        row[secondValue] = calcEntry.secondValue
                        row[operation] = calcEntry.operation
                        row[result] = calcEntry.result
                        row[username] = calcEntry.username
                        row[createdAt] = OffsetDateTime.now(ZoneOffset.UTC)
                    }
                }

                sut.deleteByIdIn(calcEntryIdsToDelete)

                Then("it should delete the specified entry") {
                    val thrown =
                        transaction {
                            shouldThrow<EntityNotFoundException> {
                                CalcEntryEntity[1L]
                            }
                        }
                    thrown.message shouldBe "Entity CalcEntryEntity, id=1 not found in the database"
                }
            }

            When("deleting all entries created before a specific time with a null username") {
                val calcEntry = calcEntryInTest(1L)

                transaction {
                    CalcEntryTable.insert { row ->
                        row[firstValue] = calcEntry.firstValue
                        row[secondValue] = calcEntry.secondValue
                        row[operation] = calcEntry.operation
                        row[result] = calcEntry.result
                        row[username] = null
                        row[createdAt] =
                            OffsetDateTime
                                .now(ZoneOffset.UTC)
                                .minus(1.hours.toJavaDuration())
                    }

                    CalcEntryTable.selectAll().count() shouldBe 1
                }

                val deleteRecordsBefore =
                    OffsetDateTime
                        .now(ZoneOffset.UTC)
                        .minus(5.minutes.toJavaDuration())

                sut.deleteAllByCreatedAtBeforeAndUsernameNull(deleteRecordsBefore)

                Then("it should delete all matching entries") {
                    transaction {
                        CalcEntryTable.selectAll().count() shouldBe 0
                    }
                }
            }
        }
    })
