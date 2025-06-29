package no.vicx.ktor.util

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
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
