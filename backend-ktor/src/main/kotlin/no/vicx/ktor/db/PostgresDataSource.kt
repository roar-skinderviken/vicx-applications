package no.vicx.ktor.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database

object PostgresDataSource {
    private var dataSource: HikariDataSource? = null

    fun getDataSource(config: ApplicationConfig): HikariDataSource {
        return if (dataSource == null) {
            dataSource = configureDb(config)
            dataSource!!
        } else {
            dataSource!!
        }
    }

    private fun configureDb(config: ApplicationConfig): HikariDataSource {
        val user = config.property("postgres.user").getString()
        val pw = config.property("postgres.password").getString()

        return HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.property("postgres.jdbc-url").getString()
            maximumPoolSize = 20
            isAutoCommit = true
            initializationFailTimeout = 5000
            minimumIdle = 1
            username = user
            password = pw
            validate()
        })
    }
}

fun Application.configureDatabase() {
    val dataSource = PostgresDataSource.getDataSource(environment.config)
    Database.connect(dataSource)
    install(FlywayPlugin) {
        this.dataSource = dataSource
    }
}
