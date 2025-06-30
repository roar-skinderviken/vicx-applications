package no.vicx.ktor.plugins

import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import com.sksamuel.cohort.db.DatabaseConnectionHealthCheck
import com.sksamuel.cohort.threads.ThreadDeadlockHealthCheck
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.Dispatchers
import javax.sql.DataSource
import kotlin.time.Duration.Companion.seconds

suspend fun Application.configureHealth() {
    val dataSource: DataSource = dependencies.resolve()

    val livenessChecks =
        HealthCheckRegistry(Dispatchers.Default) {
            // detects if threads are mutually blocked on each other's locks
            register(
                check = ThreadDeadlockHealthCheck(),
                initialDelay = 1.seconds,
                checkInterval = 10.seconds,
            )
        }

    val readinessChecks =
        HealthCheckRegistry(Dispatchers.Default) {
            register(
                check = DatabaseConnectionHealthCheck(dataSource),
                initialDelay = 1.seconds,
                checkInterval = 10.seconds,
            )
        }

    install(Cohort) {
        healthcheck("/liveness", livenessChecks)
        healthcheck("/readiness", readinessChecks)
    }
}
