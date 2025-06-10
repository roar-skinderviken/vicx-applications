package no.vicx.ktor.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.ktor.db.FlywayPlugin
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.model.CalculatorOperation
import no.vicx.ktor.db.table.CalcEntryTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.sql.DataSource

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */

object TestDb {
    val postgres: DataSource by lazy {
        EmbeddedPostgres.start().postgresDatabase
            .also { Database.connect(it) }
    }
}

fun Application.connectToPostgres(embedded: Boolean): Connection =
    if (embedded) {
        install(FlywayPlugin) {
            dataSource = TestDb.postgres
        }

        fun calcEntries(size: Int) = List(size) { index ->
            CalcEntry(
                index.toLong(), 42, 43, CalculatorOperation.PLUS,
                85, "~username~", LocalDateTime.now().plusSeconds(index.toLong()).toKotlinLocalDateTime()
            )
        }

        transaction {
            // add some initial data for localhost testing
            calcEntries(42).forEach { calcEntry ->
                CalcEntryTable.insert { row ->
                    row[firstValue] = calcEntry.firstValue
                    row[secondValue] = calcEntry.secondValue
                    row[operation] = calcEntry.operation
                    row[result] = calcEntry.result
                    row[username] = calcEntry.username
                    row[createdAt] = calcEntry.createdAt.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
                }
            }

            // insert a default user, this is aligned with what's in Spring Auth Server
            VicxUserEntity.new(1L) {
                username = "user1"
                name = "John Doe"
                email = "user1@example.com"
                password = BCryptPasswordEncoder().encode("password")
            }
        }

        TestDb.postgres.connection
    } else {
        val url = environment.config.property("postgres.url").getString()
        log.info("Connecting to postgres database at $url")
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }

