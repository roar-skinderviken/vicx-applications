package no.vicx.ktor.esport

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.vicx.ktor.esport.vm.EsportMatchVm
import no.vicx.ktor.esport.vm.MatchType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EsportServiceTest {
    private val esportClient: EsportClient = mockk()
    private val sut = EsportService(esportClient)

    @Test
    fun `given data on remote server then expect result with both running and upcoming matches`() {
        val expectedRunningMatches = createMatches(MatchType.RUNNING)
        val expectedUpcomingMatches = createMatches(MatchType.UPCOMING)

        coEvery { esportClient.getMatches(MatchType.RUNNING) } returns expectedRunningMatches
        coEvery { esportClient.getMatches(MatchType.UPCOMING) } returns expectedUpcomingMatches

        val result = runBlocking { sut.getMatches() }

        assertEquals(expectedRunningMatches, result.runningMatches)
        assertEquals(expectedUpcomingMatches, result.upcomingMatches)
    }

    @Test
    fun `given cached data then expect no further calls to esportClient`() {
        coEvery { esportClient.getMatches(MatchType.RUNNING) } returns createMatches(MatchType.RUNNING)
        coEvery { esportClient.getMatches(MatchType.UPCOMING) } returns createMatches(MatchType.UPCOMING)

        runBlocking { sut.getMatches() }
        runBlocking { sut.getMatches() }

        coVerify(exactly = 1) { esportClient.getMatches(MatchType.RUNNING) }
        coVerify(exactly = 1) { esportClient.getMatches(MatchType.UPCOMING) }
    }

    companion object {
        fun createMatches(matchType: MatchType): List<EsportMatchVm> =
            listOf(
                EsportMatchVm(1, "~match-1~", "beginAt", matchType.toString()),
            )
    }
}
