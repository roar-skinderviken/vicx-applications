package no.vicx.ktor.util

import io.ktor.server.testing.ApplicationTestBuilder
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object TestDb {
    val postgres: DataSource by lazy {
        EmbeddedPostgres.start().postgresDatabase.also { Database.connect(it) }
    }
}

fun configureTestDb(): DataSource {
    val flyway =
        Flyway
            .configure()
            .cleanDisabled(false)
            .dataSource(TestDb.postgres)
            .locations("classpath:db.migration")
            .load()

    flyway.clean()
    flyway.migrate()

    return TestDb.postgres
}

fun ApplicationTestBuilder.insertTestData(block: () -> Unit) =
    application {
        transaction {
            // addLogger(StdOutSqlLogger)
            block()
        }
    }
