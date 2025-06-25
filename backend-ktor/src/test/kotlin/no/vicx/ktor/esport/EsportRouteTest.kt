package no.vicx.ktor.esport

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import no.vicx.ktor.esport.EsportServiceTest.Companion.createMatches
import no.vicx.ktor.esport.vm.EsportVm
import no.vicx.ktor.esport.vm.MatchType
import no.vicx.ktor.user.UserTestConstants.API_ESPORT
import no.vicx.ktor.util.RouteTestContext

class EsportRouteTest :
    BehaviorSpec({
        coroutineTestScope = true

        Given("configured routing for esport") {
            val routeTestApplication = RouteTestContext()
            coEvery { routeTestApplication.esportService.getMatches() } returns expectedEsportVm

            When("calling GET /api/esport") {
                val response =
                    routeTestApplication.runInTestApplicationContext { httpClient ->
                        httpClient.get(API_ESPORT)
                    }

                Then("it should return status OK and the expected body") {
                    assertSoftly(response) {
                        status shouldBe HttpStatusCode.OK
                        body<EsportVm>() shouldBe expectedEsportVm
                    }
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
