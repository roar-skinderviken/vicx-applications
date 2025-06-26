package no.vicx.ktor.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.ktor.db.FlywayPlugin
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.model.CalculatorOperation
import no.vicx.ktor.db.table.CalcEntryTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.sql.DataSource

object TestDb {
    val postgres: DataSource by lazy {
        EmbeddedPostgres
            .start()
            .postgresDatabase
            .also { Database.connect(it) }
    }
}

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
 * @param embedded -- if `true` defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [DataSource] that represent connection to the database.
 * */
fun Application.connectToPostgres(embedded: Boolean): DataSource =
    if (embedded) {
        install(FlywayPlugin) {
            dataSource = TestDb.postgres
        }

        fun calcEntries(size: Int) =
            List(size) { index ->
                CalcEntry(
                    index.toLong(),
                    42,
                    43,
                    CalculatorOperation.PLUS,
                    85,
                    "~username~",
                    LocalDateTime.now().plusSeconds(index.toLong()).toKotlinLocalDateTime(),
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
                    row[createdAt] = OffsetDateTime.now()
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

        TestDb.postgres
    } else {
        val schema = environment.config.property("postgres.schema").getString()

        val hikariConfig =
            HikariConfig().apply {
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = environment.config.property("postgres.url").getString()
                username = environment.config.property("postgres.user").getString()
                password = environment.config.property("postgres.password").getString()
                this.schema = schema
                maximumPoolSize = 20
                isAutoCommit = true
                initializationFailTimeout = 5000
                minimumIdle = 1
            }

        val hikariDataSource = HikariDataSource(hikariConfig)

        Database.connect(hikariDataSource)

        install(FlywayPlugin) {
            dataSource = hikariDataSource
            defaultSchema = schema
        }

        hikariDataSource
    }
