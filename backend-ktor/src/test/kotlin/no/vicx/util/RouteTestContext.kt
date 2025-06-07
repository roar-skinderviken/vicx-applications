package no.vicx.util

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.mockk
import no.vicx.db.repository.UserRepository
import no.vicx.esport.EsportService
import no.vicx.plugins.configureRestApi
import no.vicx.plugins.configureStatusPage
import no.vicx.user.service.RecaptchaClient
import no.vicx.user.service.UserService
import no.vicx.util.SecurityTestUtils.configureTestSecurity
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class RouteTestContext(
    val esportService: EsportService = mockk(),
    val recaptchaClient: RecaptchaClient = mockk(),
    val userRepository: UserRepository = mockk()
) {
    fun <T : Any> runInTestApplicationContext(block: suspend (HttpClient) -> T): T {
        lateinit var result: T

        testApplication {
            application {
                configureStatusPage()
                configureTestSecurity()
                configureRestApi(
                    esportService,
                    UserService(recaptchaClient, userRepository)
                )
            }

            val httpClient = createClient {
                install(ClientContentNegotiation) { json() }
            }

            result = block(httpClient)
        }

        return result
    }
}