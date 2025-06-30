package no.vicx.ktor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.mockk.clearAllMocks
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

abstract class RouteTestBase(
    body: RouteTestBase.() -> Unit,
) : BehaviorSpec() {
    val mockUserRepository: UserRepository = mockk()
    val mockUserImageRepository: UserImageRepository = mockk()
    val mockEsportService: EsportService = mockk()
    val mockRecaptchaClient: RecaptchaClient = mockk()

    override suspend fun beforeContainer(testCase: TestCase) {
        if (!testCase.isRootTest()) {
            clearAllMocks()
        }
    }

    init {
        this.body()
    }

    fun <T : Any> withTestApplicationContext(block: suspend (HttpClient) -> T): T {
        lateinit var result: T

        testApplication {
            application {
                dependencies {
                    provide<UserRepository> { mockUserRepository }
                    provide<UserImageRepository> { mockUserImageRepository }
                    provide<EsportService> { mockEsportService }
                    provide<RecaptchaClient> { mockRecaptchaClient }
                    provide<UserService> { UserService(resolve(), resolve()) }
                    provide<UserImageService> { UserImageService(resolve(), resolve(), resolve()) }
                }

                configureStatusPage()
                configureTestSecurity()
                configureRestApi(
                    esportService = dependencies.resolve(),
                    userService = dependencies.resolve(),
                    userImageService = dependencies.resolve(),
                )
            }

            result =
                block(
                    createClient {
                        install(ContentNegotiation) { json() }
                    },
                )
        }

        return result
    }
}
