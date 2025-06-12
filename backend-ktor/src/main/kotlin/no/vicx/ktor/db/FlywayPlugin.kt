package no.vicx.ktor.db

import io.ktor.events.EventDefinition
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import javax.sql.DataSource

val FlywayMigrationStarting: EventDefinition<Application> = EventDefinition()
val FlywayMigrationFinished: EventDefinition<Application> = EventDefinition()

class FlywayPluginConfig(
    var dataSource: DataSource? = null,
    var defaultSchema: String? = "public"
)

val FlywayPlugin: ApplicationPlugin<FlywayPluginConfig> = createApplicationPlugin("Flyway", ::FlywayPluginConfig) {
    val dataSource = requireNotNull(pluginConfig.dataSource) { "DataSource is required for Flyway" }
    val defaultSchema = requireNotNull(pluginConfig.defaultSchema) { "Schema is required for Flyway" }
    val logger = LoggerFactory.getLogger("FlywayPlugin::class.java")

    application.monitor.raise(FlywayMigrationStarting, application)
    logger.info("Starting Flyway migration")

    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db.migration")
        .defaultSchema(defaultSchema)
        // .validateMigrationNaming(true) TODO: Some issues with Flyway plugin in JAR
        .load()
        .migrate()


    logger.info("Flyway migration finished")
    application.monitor.raise(FlywayMigrationFinished, application)
}