package no.vicx.ktor.esport

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.vicx.ktor.esport.vm.EsportMatchVm
import no.vicx.ktor.esport.vm.MatchType

class EsportServiceTest :
    BehaviorSpec({
        Given("EsportService with mocked EsportClient") {
            val mockEsportClient: EsportClient = mockk()
            val sut = EsportService(mockEsportClient)

            When("calling getMatches with data on remote server") {
                val expectedRunningMatches = createMatches(MatchType.RUNNING)
                val expectedUpcomingMatches = createMatches(MatchType.UPCOMING)

                coEvery { mockEsportClient.getMatches(MatchType.RUNNING) } returns expectedRunningMatches
                coEvery { mockEsportClient.getMatches(MatchType.UPCOMING) } returns expectedUpcomingMatches

                val result = sut.getMatches()

                Then("expect result with both running and upcoming matches") {
                    assertSoftly(result) {
                        runningMatches shouldBe expectedRunningMatches
                        upcomingMatches shouldBe expectedUpcomingMatches
                    }
                }
            }

            When("calling getMatches a second time") {
                val expectedRunningMatches = createMatches(MatchType.RUNNING)
                val expectedUpcomingMatches = createMatches(MatchType.UPCOMING)

                coEvery { mockEsportClient.getMatches(MatchType.RUNNING) } returns expectedRunningMatches
                coEvery { mockEsportClient.getMatches(MatchType.UPCOMING) } returns expectedUpcomingMatches

                sut.getMatches()
                sut.getMatches()

                Then("expect cached result for second call") {
                    coVerify(exactly = 1) {
                        mockEsportClient.getMatches(MatchType.RUNNING)
                        mockEsportClient.getMatches(MatchType.UPCOMING)
                    }
                }
            }
        }
    }) {
    companion object {
        fun createMatches(matchType: MatchType): List<EsportMatchVm> =
            listOf(
                EsportMatchVm(1, "~match-1~", "beginAt", matchType.toString()),
            )
    }
}
