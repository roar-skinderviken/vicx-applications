package no.vicx.ktor.esport

import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.esport.EsportServiceTest.Companion.createMatches
import no.vicx.ktor.esport.vm.EsportVm
import no.vicx.ktor.esport.vm.MatchType
import no.vicx.ktor.user.UserTestConstants.API_ESPORT
import org.koin.test.inject

class EsportRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            val mockEsportService by inject<EsportService>()

            When("retrieving esport matches from /api/esport") {
                coEvery { mockEsportService.getMatches() } returns expectedEsportVm

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_ESPORT)
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK
                }

                And("the response body should contain JSON that can be deserialized to EsportVm") {
                    response.body<EsportVm>() shouldBe expectedEsportVm
                }
            }
        }
    }) {
    companion object {
        val expectedEsportVm =
            EsportVm(
                createMatches(MatchType.RUNNING),
                createMatches(MatchType.UPCOMING),
            )
    }
}
