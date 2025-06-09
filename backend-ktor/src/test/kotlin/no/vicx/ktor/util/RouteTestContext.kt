package no.vicx.ktor.util

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.mockk
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.esport.EsportService
import no.vicx.ktor.plugins.configureRestApi
import no.vicx.ktor.plugins.configureStatusPage
import no.vicx.ktor.user.service.RecaptchaClient
import no.vicx.ktor.user.service.UserImageService
import no.vicx.ktor.user.service.UserService
import no.vicx.ktor.util.SecurityTestUtils.configureTestSecurity
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class RouteTestContext(
    val esportService: EsportService = mockk(),
    val recaptchaClient: RecaptchaClient = mockk(),
    val userRepository: UserRepository = mockk(),
    val userImageRepository: UserImageRepository = mockk(),
) {
    fun <T : Any> runInTestApplicationContext(block: suspend (HttpClient) -> T): T {
        lateinit var result: T

        val userService = UserService(recaptchaClient, userRepository)

        testApplication {
            application {
                configureStatusPage()
                configureTestSecurity()
                configureRestApi(
                    esportService,
                    userService,
                    UserImageService(userService, userRepository, userImageRepository)
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