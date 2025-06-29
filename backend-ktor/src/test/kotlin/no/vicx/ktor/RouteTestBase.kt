package no.vicx.ktor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinLifecycleMode
import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.json
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
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

abstract class RouteTestBase(
    body: RouteTestBase.() -> Unit,
) : BehaviorSpec(),
    KoinTest {
    override fun extensions() = listOf(KoinExtension(module = koinTestModule, mode = KoinLifecycleMode.Root))

    override suspend fun beforeContainer(testCase: TestCase) {
        if (!testCase.isRootTest()) {
            clearAllMocks()
        }
    }

    init {
        this.body()
    }

    companion object {
        val koinTestModule =
            module {
                single<UserRepository> { mockk() }
                single<UserImageRepository> { mockk() }
                single<EsportService> { mockk() }
                single<RecaptchaClient> { mockk() }
                single { UserService(get(), get()) }
                single { UserImageService(get(), get(), get()) }
            }

        fun <T : Any> withTestApplicationContext(callback: suspend (HttpClient) -> T): T {
            lateinit var result: T

            testApplication {
                application {
                    configureStatusPage()
                    configureTestSecurity()
                    configureRestApi(
                        esportService = get(),
                        userService = get(),
                        userImageService = get(),
                    )
                }

                result =
                    callback(
                        createClient {
                            install(ClientContentNegotiation) { json() }
                        },
                    )
            }

            return result
        }
    }
}
